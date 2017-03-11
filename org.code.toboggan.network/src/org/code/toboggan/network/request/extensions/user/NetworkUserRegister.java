package org.code.toboggan.network.request.extensions.user;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.user.IUserRegisterExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.user.IUserRegisterResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.UserRegisterRequest;

public class NetworkUserRegister extends AbstractNetworkExtension implements IUserRegisterExtension {
	private Logger logger = LogManager.getLogger(NetworkUserRegister.class);
	
	public NetworkUserRegister() {
		super();
	}

	@Override
	public void userRegistered(String username, String firstName, String lastName, String email, String password) {
		extMgr = NetworkExtensionManager.getInstance();
		Request registerRequest = (new UserRegisterRequest(username, firstName, lastName, email, password)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Successfully registered as " + username);
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.USER_REGISTER_REQUEST_ID, IUserRegisterResponse.class);
				for (ICoreExtension e : extensions) {
					IUserRegisterResponse p = (IUserRegisterResponse) e;
					p.userRegistered(username);
				}
			} else {
				handleRegisterError(username, firstName, lastName, email);
			}
		}, getRegisterSendHandler(username, firstName, lastName, email));
		wsMgr.sendRequest(registerRequest);
	}
	
	private void handleRegisterError(String username, String firstName, String lastName, String email) {
		logger.error("Error registering as user " + username);
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.USER_REGISTER_REQUEST_ID, IUserRegisterResponse.class);
		for (ICoreExtension e : extensions) {
			IUserRegisterResponse p = (IUserRegisterResponse) e;
			p.userRegistrationFailed(username, firstName, lastName, email);
		}
	}
	
	private IRequestSendErrorHandler getRegisterSendHandler(String username, String firstName, String lastName, String email) {
		return () -> handleRegisterError(username, firstName, lastName, email);
	}
}
