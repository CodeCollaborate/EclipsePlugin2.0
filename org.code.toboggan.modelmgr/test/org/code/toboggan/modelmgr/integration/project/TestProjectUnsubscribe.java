package org.code.toboggan.modelmgr.integration.project;

import java.io.IOException;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectUnsubscribe;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectUnsubscribe;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectUnsubscribe;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.request.extensions.project.NetworkProjectUnsubscribe;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.IResponseData;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.ProjectUnsubscribeRequest;
import clientcore.websocket.models.responses.ProjectUnsubscribeResponse;

public class TestProjectUnsubscribe extends AbstractTest {
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
		// Run API call
		ProjectUnsubscribe pUnsubscribe = new ProjectUnsubscribe(APIExtensionManager.getInstance(),
				testProject.getProjectID());
		pUnsubscribe.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectUnsubscribeRequest)) {
					return false;
				}

				req = argument;

				ProjectUnsubscribeRequest projUnsubscribeReq = (ProjectUnsubscribeRequest) req.data;
				return projUnsubscribeReq.getProjectID() == testProject.getProjectID();
			}
		}));
		IResponseData data = new ProjectUnsubscribeResponse();
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// Validate that metadata was created
		SessionStorage ss = CoreActivator.getSessionStorage();

		Assert.assertFalse("Project was still listed as subscribed in metadata",
				ss.getSubscribedIds().contains(testProject.getProjectID()));
	}
}
