package org.code.toboggan.modelmgr.integration.project;

import java.io.IOException;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectGrantPermissions;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectGrantPermissions;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.request.extensions.project.NetworkProjectGrantPermissions;
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
import clientcore.websocket.models.requests.ProjectGrantPermissionsRequest;
import clientcore.websocket.models.responses.ProjectGrantPermissionsResponse;

public class TestProjectGrantPermission extends AbstractTest {
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
	public void testSuccessfulPath() throws InterruptedException {
		// Run API call
		ProjectGrantPermissions pGrantPermissions = new ProjectGrantPermissions(APIExtensionManager.getInstance(),
				testProject.getProjectID(), testUser, 10);
		pGrantPermissions.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectGrantPermissionsRequest)) {
					return false;
				}

				req = argument;

				ProjectGrantPermissionsRequest projGrantPermissionsReq = (ProjectGrantPermissionsRequest) req.data;
				return projGrantPermissionsReq.getProjectID() == testProject.getProjectID()
						&& projGrantPermissionsReq.getGrantUsername() == testUser
						&& projGrantPermissionsReq.getPermissionLevel() == 10;
			}
		}));
		IResponseData data = new ProjectGrantPermissionsResponse();
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// Validate that metadata was created
		SessionStorage ss = CoreActivator.getSessionStorage();

		Assert.assertTrue("Could not find entry in permissions map",
				ss.getProject(testProject.getProjectID()).getPermissions().containsKey(testUser));
		Assert.assertEquals("Permissions map entry had wrong value",
				ss.getProject(testProject.getProjectID()).getPermissions().get(testUser).getPermissionLevel(), 10);
	}
}
