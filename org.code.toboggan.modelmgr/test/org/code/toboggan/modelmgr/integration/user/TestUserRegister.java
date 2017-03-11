package org.code.toboggan.modelmgr.integration.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.code.toboggan.core.api.user.UserRegister;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.user.NetworkUserRegister;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import clientcore.websocket.models.IResponseData;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.UserRegisterRequest;
import clientcore.websocket.models.responses.UserRegisterResponse;

public class TestUserRegister extends AbstractTest {
	private static final String TEST_PASSWORD = "testPassword";
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
	public void testSuccessfulPath() throws InterruptedException, UnsupportedEncodingException {
		// Run API call
		UserRegister uRegister = new UserRegister(APIExtensionManager.getInstance(), testUser, TEST_FIRST_NAME,
				TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD);
		uRegister.execute();

		// Verify user requests
		Mockito.verify(wsMgr).sendRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof UserRegisterRequest)) {
					return false;
				}

				req = argument;

				UserRegisterRequest userRegisterReq = (UserRegisterRequest) req.data;
				return userRegisterReq.getUsername().equals(testUser)
						&& userRegisterReq.getPassword().equals(TEST_PASSWORD)
						&& userRegisterReq.getFirstName().equals(TEST_FIRST_NAME)
						&& userRegisterReq.getLastName().equals(TEST_LAST_NAME)
						&& userRegisterReq.getEmail().equals(TEST_EMAIL);
			}
		}));
		IResponseData data = new UserRegisterResponse();
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);
	}
}
