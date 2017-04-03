package org.code.toboggan.modelmgr.integration.requests.user;

import java.io.IOException;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.user.UserProjects;
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
import clientcore.websocket.models.IResponseData;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.UserProjectsRequest;
import clientcore.websocket.models.responses.UserProjectsResponse;

public class TestUserProjects extends AbstractTest {
	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();
	}

	@After
	public void cleanup() throws CoreException {
		deleteTestProject();
	}

	@Test
	public void testSuccessfulPath() throws InterruptedException {
		// Run API call
		UserProjects uProjects = new UserProjects(APIExtensionManager.getInstance());
		uProjects.execute();

		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof UserProjectsRequest)) {
					return false;
				}

				req = argument;

				return true; // No fields to check
			}
		}));
		IResponseData data = new UserProjectsResponse(new Project[] { testProject });
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// Validate that metadata was created
		SessionStorage ss = CoreActivator.getSessionStorage();
		Assert.assertEquals("Project array was larger than expected", ss.getProjects().size(), 1);
		Assert.assertEquals("Project array contents was not testProject", ss.getProjects().get(0), testProject);
	}
}
