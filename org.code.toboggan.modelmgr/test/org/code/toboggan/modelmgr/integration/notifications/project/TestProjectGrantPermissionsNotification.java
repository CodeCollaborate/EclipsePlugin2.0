package org.code.toboggan.modelmgr.integration.notifications.project;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.modelmgr.integration.AbstractTest;
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
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.notifications.ProjectGrantPermissionsNotification;
import clientcore.websocket.models.requests.UserProjectsRequest;

public class TestProjectGrantPermissionsNotification extends AbstractTest {
	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();
	}

	@After
	public void cleanup() throws CoreException {
		deleteTestProject();
	}

	@Test
	public void testSuccessfulPath() throws InterruptedException, CoreException, IOException {
		createDefaultTestProject();
		createDefaultTestFiles();
		registerDefaultProject();
		registerDefaultFiles();

		SessionStorage ss = CoreActivator.getSessionStorage();

		ArgumentCaptor<String> notifHandlerResourceCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> notifHandlerMethodCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<INotificationHandler> notifHandlerHandlerCaptor = ArgumentCaptor
				.forClass(INotificationHandler.class);
		Mockito.verify(wsMgr, Mockito.atLeastOnce()).registerNotificationHandler(notifHandlerResourceCaptor.capture(),
				notifHandlerMethodCaptor.capture(), notifHandlerHandlerCaptor.capture());

		INotificationHandler notifHandler = null;
		for (int i = 0; i < notifHandlerResourceCaptor.getAllValues().size(); i++) {
			if (notifHandlerResourceCaptor.getAllValues().get(i).equals("Project")
					&& notifHandlerMethodCaptor.getAllValues().get(i).equals("GrantPermissions")) {
				notifHandler = notifHandlerHandlerCaptor.getAllValues().get(i);
			}
		}
		Assert.assertNotNull("Notification handler was null", notifHandler);

		ProjectGrantPermissionsNotification pdNotification = new ProjectGrantPermissionsNotification(testUser, 10);
		Notification notification = new Notification("Project", "GrantPermissions", testProject.getProjectID(),
				pdNotification);

		notifHandler.handleNotification(notification);

		// Check metadata was altered correctly
		Project projMeta = ss.getProject(testProject.getProjectID());

		Assert.assertTrue("testUser permission not in map", projMeta.getPermissions().containsKey(testUser));
		Assert.assertEquals("Permission username incorrect", 10,
				projMeta.getPermissions().get(testUser).getPermissionLevel());
		Assert.assertEquals("Permission username not in map", testUser,
				projMeta.getPermissions().get(testUser).getUsername());
	}

	@Test
	public void testSuccessfulPathUnknownProject()
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
					&& notifHandlerMethodCaptor.getAllValues().get(i).equals("GrantPermissions")) {
				notifHandler = notifHandlerHandlerCaptor.getAllValues().get(i);
			}
		}
		Assert.assertNotNull("Notification handler was null", notifHandler);

		ProjectGrantPermissionsNotification pdNotification = new ProjectGrantPermissionsNotification(testUser, 10);
		Notification notification = new Notification("Project", "GrantPermissions", testProject.getProjectID(),
				pdNotification);

		notifHandler.handleNotification(notification);

		CoreActivator.getExecutor().shutdown();
		Assert.assertTrue("Timed out waiting for executor to shutdown",
				CoreActivator.getExecutor().awaitTermination(5, TimeUnit.SECONDS));

		// Verify project lookup request is sent
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof UserProjectsRequest)) {
					return false;
				}

				req = argument;

				return true;
			}
		}));
	}
}
