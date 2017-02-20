package org.code.toboggan.network.request.extensions.user;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.user.IUserRegisterExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.user.IUserRegisterResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.UserRegisterRequest;

public class NetworkUserRegister implements IUserRegisterExtension {
	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkUserRegister.class);
	
	public NetworkUserRegister() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}

	@Override
	public void userRegistered(String username, String firstName, String lastName, String email, String password) {
		Request registerRequest = (new UserRegisterRequest(username, firstName, lastName, email, password)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Successfully registered as " + username);
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.USER_REGISTER_ID);
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.USER_REGISTER_ID);
		for (ICoreExtension e : extensions) {
			IUserRegisterResponse p = (IUserRegisterResponse) e;
			p.userRegistrationFailed(username, firstName, lastName, email);
		}
	}
	
	private IRequestSendErrorHandler getRegisterSendHandler(String username, String firstName, String lastName, String email) {
		return () -> handleRegisterError(username, firstName, lastName, email);
	}
}
