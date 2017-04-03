package org.code.toboggan.modelmgr.integration.notifications.file;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

import com.fasterxml.jackson.core.JsonProcessingException;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.notifications.FileDeleteNotification;

public class TestFileDeleteNotification extends AbstractTest {
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
					&& notifHandlerMethodCaptor.getAllValues().get(i).equals("Delete")) {
				notifHandler = notifHandlerHandlerCaptor.getAllValues().get(i);
			}
		}
		Assert.assertNotNull("Notification handler was null", notifHandler);

		for (int i = 0; i < testFiles.length; i++) {
			File fData = testFiles[i];

			FileDeleteNotification fdNotification = new FileDeleteNotification();
			Notification notification = new Notification("File", "Delete", fData.getFileID(), fdNotification);

			notifHandler.handleNotification(notification);

			// Check that files were removed from metadata
			SessionStorage ss = CoreActivator.getSessionStorage();
			Project projMeta = ss.getProject(testProject.getProjectID());
			Path fPath = Paths.get(testIProject.getLocation().toFile().toPath().toString(),
					fData.getRelativePath().resolve(fData.getFilename()).toString());

			Assert.assertNull("File metadata still found by ID", projMeta.getFile(fData.getFileID()));
			Assert.assertNull("File metadata still found by path", projMeta.getFile(fPath));

			Assert.assertNull("SessionStore still has file for ID", ss.getFile(fData.getFileID()));
			Assert.assertNull("SessionStore still has file for path", ss.getFile(fPath));

			// Verify that the content exists and is correct
			IFile iFile = iProject.getFile(fData.getRelativePath().resolve(fData.getFilename()).toString());

			// Check that file exists before reading contents
			Assert.assertFalse("File still existed", iFile.exists());
		}
	}
}
