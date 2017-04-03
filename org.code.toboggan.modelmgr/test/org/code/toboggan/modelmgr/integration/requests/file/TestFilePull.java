package org.code.toboggan.modelmgr.integration.requests.file;

import java.io.IOException;
import java.io.InputStream;

import org.code.toboggan.core.api.file.FilePull;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.utils.FSUtils;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
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
import clientcore.websocket.models.requests.FilePullRequest;
import clientcore.websocket.models.responses.FilePullResponse;

public class TestFilePull extends AbstractTest {
	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();
		createDefaultTestProject();
		registerDefaultProject();
		registerDefaultFiles();
	}

	@After
	public void cleanup() throws CoreException {
		deleteTestProject();
	}

	@Test
	public void testSuccessfulPath() throws InterruptedException, IOException, CoreException {
		// Get workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject iProject = workspace.getRoot().getProject(testProject.getName());

		// Run API call
		for (int i = 0; i < testFiles.length; i++) {
			File fData = testFiles[i];
			String fContent = testFileContents[i];

			FilePull fPull = new FilePull(APIExtensionManager.getInstance(), fData.getFileID());
			fPull.execute();

			// Verify file requests
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
			String[] patches = new String[] {};
			if (fData.getFileID() == 1) {
				patches = new String[] { String.format("v1:\n0:-%d:%s:\n%d", fContent.replace("\r\n", "\n").length(),
						fContent.replace("\r\n", "\n"), fContent.replace("\r\n", "\n").length()) };
			}
			IResponseData data = new FilePullResponse(fContent.replace("\r\n", "\n").getBytes(), patches);
			Response resp = new Response(0L, 200, data);

			// Send Response
			req.getResponseHandler().handleResponse(resp);

			// Verify that the content exists and is correct
			IFile iFile = iProject.getFile(fData.getRelativePath().resolve(fData.getFilename()).toString());

			// Check that file exists before reading contents
			Assert.assertTrue("File did not exist", iFile.exists());

			String contents = "";

			try (InputStream fInputStream = iFile.getContents()) {
				contents = new String(FSUtils.inputStreamToByteArray(fInputStream));
			} catch (IOException e) {
				Assert.fail("Threw IO exception when reading file");
			}

			// Check that contents are correct.
			if (patches.length != 0) {
				Assert.assertEquals("File contents incorrect", "", contents);
				Assert.assertEquals("ShadowDocumentManager did not have the correct contents", "",
						FSActivator.getShadowDocumentManager().getShadow(fData.getFileID()));
			} else {
				Assert.assertEquals("File contents incorrect", fContent.replace("\r\n", "\n"), contents);
				Assert.assertEquals("ShadowDocumentManager did not have the correct contents",
						fContent.replace("\r\n", "\n"),
						FSActivator.getShadowDocumentManager().getShadow(fData.getFileID()));
			}
		}
	}
}
