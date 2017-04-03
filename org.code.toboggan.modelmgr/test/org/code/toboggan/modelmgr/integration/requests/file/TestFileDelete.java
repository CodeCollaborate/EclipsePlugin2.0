package org.code.toboggan.modelmgr.integration.requests.file;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.file.FileDelete;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.File;
import clientcore.websocket.models.IResponseData;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.FileDeleteRequest;
import clientcore.websocket.models.responses.FileDeleteResponse;

public class TestFileDelete extends AbstractTest {
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
		for (File fData : testFiles) {
			FileDelete fDelete = new FileDelete(APIExtensionManager.getInstance(), fData.getFileID());
			fDelete.execute();

			// Verify file requests
			Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
				@Override
				public boolean matches(Request argument) {
					if (!(argument.data instanceof FileDeleteRequest)) {
						return false;
					}

					req = argument;

					FileDeleteRequest fileDeleteReq = (FileDeleteRequest) req.data;
					return fileDeleteReq.getFileID() == fData.getFileID();
				}
			}));
			IResponseData data = new FileDeleteResponse();
			Response resp = new Response(0L, 200, data);

			// Send Response
			req.getResponseHandler().handleResponse(resp);

			// Check that files were removed from metadata
			SessionStorage ss = CoreActivator.getSessionStorage();
			Project projMeta = ss.getProject(testProject.getProjectID());
			Path fPath = Paths.get(testIProject.getLocation().toFile().toPath().toString(),
					fData.getRelativePath().resolve(fData.getFilename()).toString());

			Assert.assertNull("File metadata still found by ID", projMeta.getFile(fData.getFileID()));
			Assert.assertNull("File metadata still found by path", projMeta.getFile(fPath));

			Assert.assertNull("SessionStore still has file for ID", ss.getFile(fData.getFileID()));
			Assert.assertNull("SessionStore still has file for path", ss.getFile(fPath));
		}
	}
}
