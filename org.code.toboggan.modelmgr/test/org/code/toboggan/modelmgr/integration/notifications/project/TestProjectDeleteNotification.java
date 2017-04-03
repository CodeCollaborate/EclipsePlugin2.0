package org.code.toboggan.modelmgr.integration.notifications.project;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.modelmgr.integration.AbstractTest;
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
import clientcore.websocket.models.notifications.ProjectDeleteNotification;

public class TestProjectDeleteNotification extends AbstractTest {
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
					&& notifHandlerMethodCaptor.getAllValues().get(i).equals("Delete")) {
				notifHandler = notifHandlerHandlerCaptor.getAllValues().get(i);
			}
		}
		Assert.assertNotNull("Notification handler was null", notifHandler);

		ProjectDeleteNotification pdNotification = new ProjectDeleteNotification();
		Notification notification = new Notification("Project", "Delete", testProject.getProjectID(), pdNotification);

		notifHandler.handleNotification(notification);

		// Get workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject iProj = workspace.getRoot().getProject(testProject.getName());

		// Check metadata was deleted correctly
		SessionStorage ss = CoreActivator.getSessionStorage();
		Path iProjPath = iProj.getLocation().toFile().toPath();
		Project projMeta = ss.getProject(testProject.getProjectID());

		Assert.assertNull("Project metadata was still found by ID", projMeta);
		Assert.assertNull("Project metadata was still found by path", ss.getProject(iProjPath));

		for (File fData : testFiles) {
			Path filePath = iProj.getLocation().toFile().toPath()
					.resolve(fData.getRelativePath().resolve(fData.getFilename()));

			Assert.assertNull("File was not null when searched by ID: " + fData.getFilename(),
					ss.getFile(fData.getFileID()));
			Assert.assertNull("File was not null when searched by filepath: " + fData.getFilename(),
					ss.getFile(filePath));
		}

		Assert.assertFalse("Project was still listed as subscribed",
				ss.getSubscribedIds().contains(testProject.getProjectID()));
	}
}
