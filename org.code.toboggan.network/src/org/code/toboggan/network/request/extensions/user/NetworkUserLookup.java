package org.code.toboggan.network.request.extensions.user;

import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.user.IUserLookupExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.user.IUserLookupResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.User;
import clientcore.websocket.models.requests.UserLookupRequest;
import clientcore.websocket.models.responses.UserLookupResponse;

public class NetworkUserLookup implements IUserLookupExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkUserLookup.class);
	
	public NetworkUserLookup() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void userLookup(String username) {
		String[] usernames = {username};
		Request lookupRequest = (new UserLookupRequest(usernames)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Successfully looked up user " + username);
				User user = ((UserLookupResponse) response.getData()).getUsers()[0];
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.USER_LOOKUP_ID, IUserLookupResponse.class);
				for (ICoreExtension e : extensions) {
					IUserLookupResponse p = (IUserLookupResponse) e;
					p.userFound(user);
				}
			} else {
				handleLookupError(username);
			}
		}, getLookupSendHandler(username));
		wsMgr.sendAuthenticatedRequest(lookupRequest);
	}

	private void handleLookupError(String username) {
		logger.error("Error looking up user " + username);
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.USER_LOOKUP_ID, IUserLookupResponse.class);
		for (ICoreExtension e : extensions) {
			IUserLookupResponse p = (IUserLookupResponse) e;
			p.userLookupFailed(username);
		}
	}
	
	private IRequestSendErrorHandler getLookupSendHandler(String username) {
		return () -> handleLookupError(username);
	}
}
