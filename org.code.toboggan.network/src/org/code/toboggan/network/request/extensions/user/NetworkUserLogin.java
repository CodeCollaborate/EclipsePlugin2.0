package org.code.toboggan.network.request.extensions.user;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.user.IUserLoginExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.user.IUserLoginResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.UserLoginRequest;

public class NetworkUserLogin implements IUserLoginExtension {
	
	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkUserLogin.class);
	
	public NetworkUserLogin() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}

	@Override
	public void userLogin(String username, String password) {
		Request loginRequest = (new UserLoginRequest(username, password)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Successfully logged in as " + username);
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.USER_LOGIN_ID);
				for (ICoreExtension e : extensions) {
					IUserLoginResponse p = (IUserLoginResponse) e;
					p.loggedIn(username);
				}
			} else {
				handleLoginError(username);
			}
		}, getLoginSendHandler(username));
		wsMgr.sendRequest(loginRequest);
	}
	
	private void handleLoginError(String username) {
		logger.error("Error logging in as user " + username + " with password " + "************");
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.USER_LOGIN_ID);
		for (ICoreExtension e : extensions) {
			IUserLoginResponse p = (IUserLoginResponse) e;
			p.loginFailed(username);
		}
	}
	
	private IRequestSendErrorHandler getLoginSendHandler(String username) {
		return () -> handleLoginError(username);
	}
}
