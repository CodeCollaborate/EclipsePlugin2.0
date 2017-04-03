package org.code.toboggan.modelmgr.integration.requests.file;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.file.FileCreate;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.eclipse.core.resources.IFile;
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
import clientcore.websocket.models.requests.FileCreateRequest;
import clientcore.websocket.models.responses.FileCreateResponse;

public class TestFileCreate extends AbstractTest {
	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();
		createDefaultTestProject();
		registerDefaultProject();
		createDefaultTestFiles();
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
			Path filePath = fData.getRelativePath().resolve(fData.getFilename());
			IFile iFile = testIProject.getFile(filePath.toString());

			FileCreate fCreate = new FileCreate(APIExtensionManager.getInstance(), fData.getFilename(),
					iFile.getLocation().toFile().toPath(), fData.getProjectID(), fContent.getBytes());
			fCreate.execute();

			// Verify file requests
			Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
				@Override
				public boolean matches(Request argument) {
					if (!(argument.data instanceof FileCreateRequest)) {
						return false;
					}

					req = argument;

					FileCreateRequest fileCreateReq = (FileCreateRequest) req.data;
					return fileCreateReq.getName().equals(fData.getFilename())
							&& fileCreateReq.getProjectID() == fData.getProjectID()
							&& Paths.get(fileCreateReq.getRelativePath()).equals(fData.getRelativePath())
							&& Arrays.equals(fileCreateReq.getFileBytes(), fContent.replace("\r\n", "\n").getBytes());
				}
			}));
			IResponseData data = new FileCreateResponse((int) fData.getFileID());
			Response resp = new Response(0L, 200, data);

			// Send Response
			req.getResponseHandler().handleResponse(resp);

			// Check that files were added to metadata
			SessionStorage ss = CoreActivator.getSessionStorage();
			Project projMeta = ss.getProject(testProject.getProjectID());
			Path fPath = Paths.get(testIProject.getLocation().toFile().toPath().toString(),
					fData.getRelativePath().resolve(fData.getFilename()).toString());
			File fileMeta = projMeta.getFile(fData.getFileID());

			Assert.assertNotNull("File metadata could not be found by ID", fileMeta);
			Assert.assertNotNull("File metadata could not be found by path", projMeta.getFile(fPath));
			Assert.assertEquals("File metadata different for ID vs path", fileMeta, projMeta.getFile(fPath));
			Assert.assertEquals("FileID was incorrect in metadata", fData.getFileID(), fileMeta.getFileID());
			Assert.assertEquals("File version was incorrect in metadata", fData.getFileVersion(),
					fileMeta.getFileVersion());
			Assert.assertEquals("Filename was incorrect in metadata", fData.getFilename(), fileMeta.getFilename());
			Assert.assertEquals("File relative path was incorrect in metadata", fData.getRelativePath(),
					fileMeta.getRelativePath());
			Assert.assertEquals("File projectID was incorrect in metadata", fData.getProjectID(),
					fileMeta.getProjectID());

			Assert.assertEquals("SessionStore has different file for ID", fileMeta, ss.getFile(fData.getFileID()));
			Assert.assertEquals("SessionStore has different file for path", fileMeta, ss.getFile(fPath));

			Assert.assertEquals("ShadowDocumentManager did not have the correct contents",
					fContent.replaceAll("\r\n", "\n"),
					FSActivator.getShadowDocumentManager().getShadow(fData.getFileID()));
		}
	}
}
