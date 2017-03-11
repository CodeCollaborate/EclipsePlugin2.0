package org.code.toboggan.network.request.extensions.user;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.user.IUserLookupExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.user.IUserLookupResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.User;
import clientcore.websocket.models.requests.UserLookupRequest;
import clientcore.websocket.models.responses.UserLookupResponse;

public class NetworkUserLookup extends AbstractNetworkExtension implements IUserLookupExtension {
	private Logger logger = LogManager.getLogger(NetworkUserLookup.class);
	
	public NetworkUserLookup() {
		super();
	}
	
	@Override
	public void userLookup(String username) {
		extMgr = NetworkExtensionManager.getInstance();
		String[] usernames = {username};
		Request lookupRequest = (new UserLookupRequest(usernames)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Successfully looked up user " + username);
				User user = ((UserLookupResponse) response.getData()).users[0];
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.USER_LOOKUP_REQUEST_ID, IUserLookupResponse.class);
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.USER_LOOKUP_REQUEST_ID, IUserLookupResponse.class);
		for (ICoreExtension e : extensions) {
			IUserLookupResponse p = (IUserLookupResponse) e;
			p.userLookupFailed(username);
		}
	}
	
	private IRequestSendErrorHandler getLookupSendHandler(String username) {
		return () -> handleLookupError(username);
	}
}
