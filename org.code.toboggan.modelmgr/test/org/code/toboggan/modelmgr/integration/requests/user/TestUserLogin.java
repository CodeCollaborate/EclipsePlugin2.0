package org.code.toboggan.modelmgr.integration.requests.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.user.UserLogin;
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
import clientcore.websocket.models.requests.UserLoginRequest;
import clientcore.websocket.models.responses.UserLoginResponse;

public class TestUserLogin extends AbstractTest {
	private static final String TEST_PASSWORD = "testPassword";
	private static final String TEST_TOKEN = "testToken";
	SessionStorage ss;

	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();

		this.ss = CoreActivator.getSessionStorage();
	}

	@After
	public void cleanup() throws CoreException {
		deleteTestProject();
	}

	@Test
	public void testSuccessfulPath() throws InterruptedException, UnsupportedEncodingException {
		// Run API call
		UserLogin uLogin = new UserLogin(APIExtensionManager.getInstance(), testUser, TEST_PASSWORD);
		uLogin.execute();

		// Verify user requests
		Mockito.verify(wsMgr).sendRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof UserLoginRequest)) {
					return false;
				}

				req = argument;

				UserLoginRequest userLoginReq = (UserLoginRequest) req.data;
				return userLoginReq.getUsername().equals(testUser) && userLoginReq.getPassword().equals(TEST_PASSWORD);
			}
		}));
		IResponseData data = new UserLoginResponse(TEST_TOKEN);
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// Verify login data was set
		Mockito.verify(wsMgr).setAuthInfo(Mockito.argThat(new ArgumentMatcher<String>() {
			@Override
			public boolean matches(String argument) {
				return argument.equals(testUser);

			}
		}), Mockito.argThat(new ArgumentMatcher<String>() {
			@Override
			public boolean matches(String argument) {
				return argument.equals(TEST_TOKEN);
			}
		}));

		Assert.assertEquals("Username was incorrect", testUser, ss.getUsername());
	}
}
