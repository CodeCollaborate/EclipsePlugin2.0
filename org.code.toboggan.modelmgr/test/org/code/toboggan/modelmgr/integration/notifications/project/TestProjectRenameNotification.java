package org.code.toboggan.modelmgr.integration.notifications.project;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import com.fasterxml.jackson.core.JsonProcessingException;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.notifications.ProjectRenameNotification;

public class TestProjectRenameNotification extends AbstractTest {
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
		ArgumentCaptor<String> notifHandlerResourceCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> notifHandlerMethodCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<INotificationHandler> notifHandlerHandlerCaptor = ArgumentCaptor
				.forClass(INotificationHandler.class);
		Mockito.verify(wsMgr, Mockito.atLeastOnce()).registerNotificationHandler(notifHandlerResourceCaptor.capture(),
				notifHandlerMethodCaptor.capture(), notifHandlerHandlerCaptor.capture());

		INotificationHandler notifHandler = null;
		for (int i = 0; i < notifHandlerResourceCaptor.getAllValues().size(); i++) {
			if (notifHandlerResourceCaptor.getAllValues().get(i).equals("Project")
					&& notifHandlerMethodCaptor.getAllValues().get(i).equals("Rename")) {
				notifHandler = notifHandlerHandlerCaptor.getAllValues().get(i);
			}
		}
		Assert.assertNotNull("Notification handler was null", notifHandler);

		ProjectRenameNotification prNotification = new ProjectRenameNotification(testProject.getName() + "_renamed");
		Notification notification = new Notification("Project", "Rename", testProject.getProjectID(), prNotification);

		notifHandler.handleNotification(notification);

		// Get workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject iProj = workspace.getRoot().getProject(testProject.getName());

		// Check metadata was altered correctly
		SessionStorage ss = CoreActivator.getSessionStorage();
		Path iProjPath = iProj.getLocation().toFile().toPath();
		Project projMeta = ss.getProject(testProject.getProjectID());

		Assert.assertNotNull("Project metadata was not found by ID", projMeta);
		Assert.assertNotNull("Project metadata was not found by path", ss.getProject(iProjPath));

		for (File fData : testFiles) {
			Path filePath = iProj.getLocation().toFile().toPath()
					.resolve(fData.getRelativePath().resolve(fData.getFilename()));

			Assert.assertNotNull("File was null when searched by ID: " + fData.getFilename(),
					ss.getFile(fData.getFileID()));
			Assert.assertNotNull("File was null when searched by filepath: " + fData.getFilename(),
					ss.getFile(filePath));

			// Verify that the content exists and is correct
			IFile iFile = iProj.getFile(fData.getRelativePath().resolve(fData.getFilename()).toString());

			// Check that new file exists before reading contents
			Assert.assertTrue("File did not exist", iFile.exists());
		}
	}
}
