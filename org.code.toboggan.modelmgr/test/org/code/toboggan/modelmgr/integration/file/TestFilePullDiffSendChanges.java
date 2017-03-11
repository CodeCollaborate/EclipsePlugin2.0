package org.code.toboggan.modelmgr.integration.file;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.code.toboggan.core.api.file.FilePullDiffSendChanges;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.filesystem.extensions.file.FSFilePullDiffSendChanges;
import org.code.toboggan.modelmgr.extensions.file.ModelMgrFileChange;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.request.extensions.file.NetworkFileChange;
import org.code.toboggan.network.request.extensions.file.NetworkFilePullDiffSendChanges;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import clientcore.websocket.models.File;
import clientcore.websocket.models.IResponseData;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.FileChangeRequest;
import clientcore.websocket.models.requests.FilePullRequest;
import clientcore.websocket.models.responses.FileChangeResponse;
import clientcore.websocket.models.responses.FilePullResponse;

public class TestFilePullDiffSendChanges extends AbstractTest {
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
	public void testSuccessfulPath() throws InterruptedException {
		// Run API call
		for (int i = 0; i < testFiles.length; i++) {
			File fData = testFiles[i];
			String fContent = testFileContents[i];

			FilePullDiffSendChanges fPullDiffSendChanges = new FilePullDiffSendChanges(
					APIExtensionManager.getInstance(), fData.getFileID());
			fPullDiffSendChanges.execute();

			// Verify requests
			Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
				@Override
				public boolean matches(Request argument) {
					if (!(argument.data instanceof FilePullRequest)) {
						return false;
					}
					
					req = argument;

					FilePullRequest filePullReq = (FilePullRequest) req.data;
					return filePullReq.getFileID() == fData.getFileID();
				}
			}));
			IResponseData data = new FilePullResponse(new byte[] {}, new String[] {});
			Response resp = new Response(0L, 200, data);

			// Send Response
			req.getResponseHandler().handleResponse(resp);

			Thread.sleep(250);

			// Verify diffs are sent - should be entire file contents
			Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
				@Override
				public boolean matches(Request argument) {
					if (!(argument.data instanceof FileChangeRequest)) {
						return false;
					}
					
					req = argument;

					String content = fContent.replace("\r\n", "\n");

					FileChangeRequest fileChangeReq = (FileChangeRequest) req.data;

					try {
						return fileChangeReq.getFileID() == fData.getFileID() && Arrays
								.equals(fileChangeReq.getChanges(), new String[] { String.format("v1:\n0:+%d:%s",
										content.length(), URLEncoder.encode(content, "UTF-8")) });
					} catch (UnsupportedEncodingException e) {
						Assert.fail("UnsupportedEncodingException for UTF-8 encoding");
					}
					return false;
				}
			}));
			data = new FileChangeResponse(2, ((FileChangeRequest) req.data).getChanges(), new String[] {});
			resp = new Response(0L, 200, data);

			// Send Response
			req.getResponseHandler().handleResponse(resp);
		}
	}
}
