package org.code.toboggan.modelmgr.extensions.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.network.request.extensionpoints.user.IUserLoginResponse;

import clientcore.dataMgmt.SessionStorage;

public class ModelMgrUserLogin implements IUserLoginResponse {
	Logger logger = LogManager.getLogger(ModelMgrUserLogin.class);

	@Override
	public void loggedIn(String username, String authToken) {
		logger.debug("Setting username and auth token in storage");
		SessionStorage ss = CoreActivator.getSessionStorage();

		// Only set this the first time, to make sure the fetchAndSubscribeAll
		// is only fired the first time.
		if (ss.getUsername() == null || !ss.getUsername().equals(username)) {
			ss.setUsername(username);
		}
		ss.setAuthenticationToken(authToken);
	}

	@Override
	public void loginFailed(String username) {
		// do nothing
	}

}
