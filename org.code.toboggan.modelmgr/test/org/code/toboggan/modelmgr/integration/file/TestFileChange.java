package org.code.toboggan.modelmgr.integration.file;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.file.FileChange;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.extensions.file.ModelMgrFileChange;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.request.extensions.file.NetworkFileChange;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import clientcore.dataMgmt.SessionStorage;
import clientcore.patching.Patch;
import clientcore.websocket.models.File;
import clientcore.websocket.models.IResponseData;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.FileChangeRequest;
import clientcore.websocket.models.responses.FileChangeResponse;

public class TestFileChange extends AbstractTest {
	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();
		createDefaultTestProject();
		createDefaultTestFiles();
		registerDefaultProject();
		registerDefaultFiles();
	}

	@After
	public void cleanup() throws CoreException {
		deleteTestProject();
	}

	@Test
	public void testSuccessfulPath() throws InterruptedException, UnsupportedEncodingException {
		// Run API call
		for (int i = 0; i < testFiles.length; i++) {
			File fData = testFiles[i];
			String fContent = testFileContents[i];

			String insertedString = "Hello\r\n";
			String removedString = "test";
			FileChange fChange = new FileChange(APIExtensionManager.getInstance(), fData.getFileID(),
					new Patch[] {
							new Patch(String.format("v1:\n%d:+%d:%s", fContent.length(), insertedString.length(),
									URLEncoder.encode(insertedString, "UTF-8"))),
							new Patch(String.format("v2:\n%d:-%d:%s", 0, removedString.length(),
									URLEncoder.encode(removedString, "UTF-8"))) },
					fContent);
			fChange.execute();

			Thread.sleep(250);

			String[] expectedPatches = new String[] {
					new Patch(String.format("v1:\n%d:+%d:%s", fContent.replace("\r\n", "\n").length(),
							insertedString.replace("\r\n", "\n").length(),
							URLEncoder.encode(insertedString.replace("\r\n", "\n"), "UTF-8"))).toString(),
					new Patch(String.format("v2:\n%d:-%d:%s", 0, removedString.length(),
							URLEncoder.encode(removedString, "UTF-8"))).toString() };

			// Verify file requests
			Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
				@Override
				public boolean matches(Request argument) {
					if (!(argument.data instanceof FileChangeRequest)) {
						return false;
					}
					
					req = argument;

					FileChangeRequest fileChangeReq = (FileChangeRequest) req.data;
					return fileChangeReq.getFileID() == fData.getFileID()
							&& Arrays.deepEquals(fileChangeReq.getChanges(), expectedPatches);
				}
			}));
			// TODO(wongb): Change this to long
			int responseFileVersion = 2;
			IResponseData data = new FileChangeResponse(responseFileVersion, expectedPatches, new String[] {});
			Response resp = new Response(0L, 200, data);

			// Send Response
			req.getResponseHandler().handleResponse(resp);

			// Check that file version was correctly updated
			SessionStorage ss = CoreActivator.getSessionStorage();
			Project projMeta = ss.getProject(testProject.getProjectID());
			File fileMeta = projMeta.getFile(fData.getFileID());

			Assert.assertEquals("File version was not incremented properly", responseFileVersion,
					fileMeta.getFileVersion());
		}
	}
}
