package org.code.toboggan.modelmgr.integration.project;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectFetchAndSubscribeAll;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectFetchAndSubscribeAll;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.request.extensions.project.NetworkProjectFetchAndSubscribeAll;
import org.code.toboggan.network.request.extensions.project.NetworkProjectSubscribe;
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
import clientcore.websocket.models.requests.ProjectSubscribeRequest;
import clientcore.websocket.models.requests.UserProjectsRequest;
import clientcore.websocket.models.responses.UserProjectsResponse;

public class TestProjectFetchAndSubscribeAll extends AbstractTest {
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
		ProjectFetchAndSubscribeAll pFetchAndSubscribeAll = new ProjectFetchAndSubscribeAll(
				APIExtensionManager.getInstance(),
				Collections.unmodifiableList(Arrays.asList(testProject.getProjectID())));
		pFetchAndSubscribeAll.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof UserProjectsRequest)) {
					return false;
				}

				req = argument;

				return true; // nothing to check here.
			}
		}));
		IResponseData data = new UserProjectsResponse(new Project[] { testProject });
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		SessionStorage ss = CoreActivator.getSessionStorage();
		Project projMeta = ss.getProject(testProject.getProjectID());
		Assert.assertNotNull("Project metadata could not be found by ID", projMeta);
		
		
		// Wait for next requests to be sent
		CoreActivator.getExecutor().shutdown();
		Assert.assertTrue("Timed out waiting for executor to shutdown",
				CoreActivator.getExecutor().awaitTermination(500, TimeUnit.SECONDS));

		// Verify project request was sent
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectSubscribeRequest)) {
					return false;
				}

				req = argument;

				ProjectSubscribeRequest projSubscribeReq = (ProjectSubscribeRequest) req.data;
				return projSubscribeReq.getProjectID() == testProject.getProjectID();
			}
		}));
	}
}
