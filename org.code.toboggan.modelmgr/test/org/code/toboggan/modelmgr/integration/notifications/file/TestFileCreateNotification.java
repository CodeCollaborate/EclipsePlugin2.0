package org.code.toboggan.modelmgr.integration.notifications.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.code.toboggan.core.CoreActivator;
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
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.File;
import clientcore.websocket.models.IResponseData;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.notifications.FileCreateNotification;
import clientcore.websocket.models.requests.FilePullRequest;
import clientcore.websocket.models.responses.FilePullResponse;

public class TestFileCreateNotification extends AbstractTest {
	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();
		createDefaultTestProject();
		registerDefaultProject();
	}

	@After
	public void cleanup() throws CoreException {
		deleteTestProject();
	}

	@Test
	public void testSuccessfulPath()
			throws InterruptedException, UnsupportedEncodingException, JsonProcessingException, CoreException {
		// Get workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject iProject = workspace.getRoot().getProject(testProject.getName());

		ArgumentCaptor<String> notifHandlerResourceCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> notifHandlerMethodCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<INotificationHandler> notifHandlerHandlerCaptor = ArgumentCaptor
				.forClass(INotificationHandler.class);
		Mockito.verify(wsMgr, Mockito.atLeastOnce()).registerNotificationHandler(notifHandlerResourceCaptor.capture(),
				notifHandlerMethodCaptor.capture(), notifHandlerHandlerCaptor.capture());

		INotificationHandler notifHandler = null;
		for (int i = 0; i < notifHandlerResourceCaptor.getAllValues().size(); i++) {
			if (notifHandlerResourceCaptor.getAllValues().get(i).equals("File")
					&& notifHandlerMethodCaptor.getAllValues().get(i).equals("Create")) {
				notifHandler = notifHandlerHandlerCaptor.getAllValues().get(i);
			}
		}
		Assert.assertNotNull("Notification handler was null", notifHandler);

		for (int i = 0; i < testFiles.length; i++) {
			File fData = testFiles[i];
			String fContent = testFileContents[i];

			FileCreateNotification fcNotification = new FileCreateNotification(fData);
			Notification notification = new Notification("File", "Create", testProject.getProjectID(), fcNotification);

			notifHandler.handleNotification(notification);

			// Verify metadata was added
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

			// Wait for next requests to be sent
			CoreActivator.getExecutor().shutdown();
			Assert.assertTrue("Timed out waiting for executor to shutdown",
					CoreActivator.getExecutor().awaitTermination(5, TimeUnit.SECONDS));
			CoreActivator.resetExecutor();

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
			IResponseData data = new FilePullResponse(fContent.getBytes(), new String[] {});
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
			Assert.assertEquals("File contents incorrect", fContent, contents);
		}
	}
}
