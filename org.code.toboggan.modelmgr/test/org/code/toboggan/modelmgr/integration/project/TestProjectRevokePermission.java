package org.code.toboggan.modelmgr.integration.project;

import java.io.IOException;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectRevokePermissions;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectRevokePermissions;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.request.extensions.project.NetworkProjectRevokePermissions;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.IResponseData;
import clientcore.websocket.models.Permission;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.ProjectRevokePermissionsRequest;
import clientcore.websocket.models.responses.ProjectRevokePermissionsResponse;

public class TestProjectRevokePermission extends AbstractTest {
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
		// Add the entry that we're going to delete
		testProject.getPermissions().put(testUser, new Permission(testUser, 10, testUser, "Now"));

		// Run API call
		ProjectRevokePermissions pRevokePermissions = new ProjectRevokePermissions(APIExtensionManager.getInstance(),
				testProject.getProjectID(), testUser);
		pRevokePermissions.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectRevokePermissionsRequest)) {
					return false;
				}

				req = argument;

				ProjectRevokePermissionsRequest projRevokePermissionsReq = (ProjectRevokePermissionsRequest) req.data;
				return projRevokePermissionsReq.getProjectID() == testProject.getProjectID()
						&& projRevokePermissionsReq.getRevokeUsername() == testUser;
			}
		}));
		IResponseData data = new ProjectRevokePermissionsResponse();
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// Validate that metadata was updated
		SessionStorage ss = CoreActivator.getSessionStorage();

		Assert.assertFalse("Found entry in permissions map",
				ss.getProject(testProject.getProjectID()).getPermissions().containsKey(testUser));
	}
}
