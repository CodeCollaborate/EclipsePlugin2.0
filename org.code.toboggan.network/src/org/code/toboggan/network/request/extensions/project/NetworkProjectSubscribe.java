package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectSubscribeExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectSubscribeResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectSubscribeRequest;

public class NetworkProjectSubscribe implements IProjectSubscribeExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectSubscribe.class);
	
	public NetworkProjectSubscribe() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void subscribed(long projectID) {
		Request subscribeRequest = (new ProjectSubscribeRequest(projectID)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Success subscribing to project: " + projectID);
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_SUBSCRIBE_ID);
				for (ICoreExtension e : extensions) {
					IProjectSubscribeResponse p = (IProjectSubscribeResponse) e;
					p.subscribed(projectID);
				}
			} else {
				handleSubscribeError(projectID);
			}
		}, getRequestSendHandler(projectID));
		wsMgr.sendAuthenticatedRequest(subscribeRequest);
	}
	
	private void handleSubscribeError(long projectID) {
		logger.error("Failed to subscribe to project: " + projectID);
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_SUBSCRIBE_ID);
		for (ICoreExtension e : extensions) {
			IProjectSubscribeResponse p = (IProjectSubscribeResponse) e;
			p.subscribeFailed(projectID);
		}
	}
	
	private IRequestSendErrorHandler getRequestSendHandler(long projectID) {
		return () -> handleSubscribeError(projectID);
	}
}
