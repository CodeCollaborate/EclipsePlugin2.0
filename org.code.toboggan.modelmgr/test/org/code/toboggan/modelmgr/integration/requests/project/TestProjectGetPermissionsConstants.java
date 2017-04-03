package org.code.toboggan.modelmgr.integration.requests.project;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectGetPermissionConstants;
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
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.ProjectGetPermissionConstantsRequest;
import clientcore.websocket.models.responses.ProjectGetPermissionConstantsResponse;

public class TestProjectGetPermissionsConstants extends AbstractTest {
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
		ProjectGetPermissionConstants pGetFiles = new ProjectGetPermissionConstants(APIExtensionManager.getInstance());
		pGetFiles.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectGetPermissionConstantsRequest)) {
					return false;
				}

				req = argument;

				return true; // nothing to check in this request
			}
		}));
		Map<String, Integer> permissionsConstants = new HashMap<>();
		permissionsConstants.put("key1", 5);
		permissionsConstants.put("key2", 7);
		IResponseData data = new ProjectGetPermissionConstantsResponse(permissionsConstants);
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// Validate that metadata was created
		SessionStorage ss = CoreActivator.getSessionStorage();

		Assert.assertNotNull("Permission map was not found", ss.getPermissionConstants());
		Assert.assertNotNull("Could not find value for key1", ss.getPermissionConstants().get("key1"));
		Assert.assertNotNull("Could not find key1 by value", ss.getPermissionConstants().inverse().get(5));
		Assert.assertNotNull("Could not find value for key2", ss.getPermissionConstants().get("key2"));
		Assert.assertNotNull("Could not find key2 by value", ss.getPermissionConstants().inverse().get(7));
	}
}
