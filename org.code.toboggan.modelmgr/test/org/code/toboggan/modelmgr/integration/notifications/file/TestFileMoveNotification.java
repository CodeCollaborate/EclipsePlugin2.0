package org.code.toboggan.modelmgr.integration.notifications.file;

import java.io.IOException;
import java.nio.file.Path;

import org.code.toboggan.core.CoreActivator;
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
import org.mockito.Mockito;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.notifications.FileMoveNotification;

public class TestFileMoveNotification extends AbstractTest {
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
					&& notifHandlerMethodCaptor.getAllValues().get(i).equals("Move")) {
				notifHandler = notifHandlerHandlerCaptor.getAllValues().get(i);
			}
		}
		Assert.assertNotNull("Notification handler was null", notifHandler);

		for (int i = 0; i < testFiles.length; i++) {
			File fData = testFiles[i];

			String newRelativePath = fData.getRelativePath().toString() + "_renamed";
			FileMoveNotification fmNotification = new FileMoveNotification(newRelativePath);
			Notification notification = new Notification("File", "Move", fData.getFileID(), fmNotification);

			notifHandler.handleNotification(notification);

			// Check that files were removed from metadata
			SessionStorage ss = CoreActivator.getSessionStorage();
			Project projMeta = ss.getProject(testProject.getProjectID());
			Path fPath = testIProject.getLocation().toFile().toPath().resolve(newRelativePath)
					.resolve(fData.getFilename());
			File fileMeta = projMeta.getFile(fData.getFileID());

			Assert.assertNotNull("File metadata could not be found by ID", fileMeta);
			Assert.assertNotNull("File metadata could not be found by path", projMeta.getFile(fPath));
			Assert.assertEquals("New relative path incorrect", newRelativePath, fileMeta.getRelativePath().toString());
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

			// Verify that the content exists and is correct
			IFile iFile = iProject.getFile(fData.getRelativePath().resolve(fData.getFilename()).toString());

			// Check that new file exists before reading contents
			Assert.assertTrue("File did not exist", iFile.exists());
		}
	}
}
