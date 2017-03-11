package org.code.toboggan.modelmgr.integration.project;

import java.io.IOException;
import java.util.Arrays;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectLookup;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectLookup;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.request.extensions.project.NetworkProjectLookup;
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
import clientcore.websocket.models.requests.ProjectLookupRequest;
import clientcore.websocket.models.responses.ProjectLookupResponse;

public class TestProjectLookup extends AbstractTest {
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
		ProjectLookup pLookup = new ProjectLookup(APIExtensionManager.getInstance(), Arrays.asList(new Long[] { testProject.getProjectID()}));
		pLookup.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectLookupRequest)) {
					return false;
				}

				req = argument;

				ProjectLookupRequest projLookupReq = (ProjectLookupRequest) req.data;
				return projLookupReq.getProjectIDs().size() == 1
						&& projLookupReq.getProjectIDs().contains(testProject.getProjectID());
			}
		}));
		IResponseData data = new ProjectLookupResponse(new Project[] { testProject });
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// Validate that metadata was created
		SessionStorage ss = CoreActivator.getSessionStorage();

		Assert.assertNotNull("Could not find project by ID", ss.getProject(testProject.getProjectID()));
		Assert.assertEquals("Inserted Project was not equal to testProject",
				ss.getProject(testProject.getProjectID()), testProject);
	}
}
