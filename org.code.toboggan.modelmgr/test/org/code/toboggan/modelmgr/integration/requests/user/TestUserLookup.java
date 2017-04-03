package org.code.toboggan.modelmgr.integration.requests.user;

import java.io.IOException;
import java.util.Arrays;

import org.code.toboggan.core.api.user.UserLookup;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import clientcore.websocket.models.IResponseData;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.User;
import clientcore.websocket.models.requests.UserLookupRequest;
import clientcore.websocket.models.responses.UserLookupResponse;

public class TestUserLookup extends AbstractTest {
	private static final String TEST_FIRST_NAME = "testFirstName";
	private static final String TEST_LAST_NAME = "testLastName";
	private static final String TEST_EMAIL = "testEmail";

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
		UserLookup uLookup = new UserLookup(APIExtensionManager.getInstance(), testUser);
		uLookup.execute();

		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof UserLookupRequest)) {
					return false;
				}

				req = argument;

				UserLookupRequest userLookupReq = (UserLookupRequest) req.data;
				return Arrays.equals(userLookupReq.getUsernames().toArray(), new String[] { testUser });
			}
		}));
		IResponseData data = new UserLookupResponse(
				new User[] { new User(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, testUser) });
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// By itself, this request does nothing; no need to check any further
	}
}
