package org.code.toboggan.network.request.extensions.user;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.user.IUserLoginExtension;
import org.code.toboggan.network.NetworkActivator;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.user.IUserLoginResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.UserLoginRequest;
import clientcore.websocket.models.responses.UserLoginResponse;

public class NetworkUserLogin extends AbstractNetworkExtension implements IUserLoginExtension {
	private Logger logger = LogManager.getLogger(NetworkUserLogin.class);
	private static TimerTask refreshTokenTimerTask;

	public NetworkUserLogin() {
		super();
	}

	@Override
	public void userLogin(String username, String password) {
		extMgr = NetworkExtensionManager.getInstance();
		Request loginRequest = (new UserLoginRequest(username, password)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Successfully logged in as " + username);
				String authToken = ((UserLoginResponse) response.getData()).token;
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.USER_LOGIN_REQUEST_ID,
						IUserLoginResponse.class);
				for (ICoreExtension e : extensions) {
					IUserLoginResponse p = (IUserLoginResponse) e;
					p.loggedIn(username, authToken);
				}

				NetworkActivator.getWSService().getWSManager().setAuthInfo(username,
						((UserLoginResponse) response.getData()).token);

				// Start refresh-timer-task; auto-refresh token every 45 minutes
				// (token validity 1 hour)
				if (refreshTokenTimerTask == null) {
					synchronized (NetworkUserLogin.class) {
						if (refreshTokenTimerTask == null) {
							refreshTokenTimerTask = new TimerTask() {
								@Override
								public void run() {
									APIFactory.createUserLogin(username, password).runAsync();
								}
							};
							new Timer().schedule(refreshTokenTimerTask, TimeUnit.MINUTES.toMillis(45),
									TimeUnit.MINUTES.toMillis(45));
						}
					}
				}
			} else {
				handleLoginError(username);
			}
		}, getLoginSendHandler(username));
		wsMgr.sendRequest(loginRequest);
	}

	private void handleLoginError(String username) {
		logger.error("Error logging in as user " + username + " with password " + "************");
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.USER_LOGIN_REQUEST_ID,
				IUserLoginResponse.class);
		for (ICoreExtension e : extensions) {
			IUserLoginResponse p = (IUserLoginResponse) e;
			p.loginFailed(username);
		}
	}

	private IRequestSendErrorHandler getLoginSendHandler(String username) {
		return () -> handleLoginError(username);
	}
}
