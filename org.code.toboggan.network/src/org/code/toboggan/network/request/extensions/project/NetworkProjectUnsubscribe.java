package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectUnsubscribeExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.project.IProjectUnsubscribeResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectUnsubscribeRequest;

public class NetworkProjectUnsubscribe extends AbstractNetworkExtension implements IProjectUnsubscribeExtension {
	private Logger logger = LogManager.getLogger(NetworkProjectUnsubscribe.class);
	
	public NetworkProjectUnsubscribe() {
		super();
	}
	
	@Override
	public void unsubscribed(long projectID) {
		extMgr = NetworkExtensionManager.getInstance();
		Request unsubRequest = (new ProjectUnsubscribeRequest(projectID)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Success unsubscribing from project: " + projectID);
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_UNSUBSCRIBE_REQUEST_ID, IProjectUnsubscribeResponse.class);
				for (ICoreExtension e : extensions) {
					IProjectUnsubscribeResponse p = (IProjectUnsubscribeResponse) e;
					p.unsubscribed(projectID);
				}
			} else {
				handleUnsubscribeError(projectID);
			}
		}, getRequestSendHandler(projectID));
		wsMgr.sendAuthenticatedRequest(unsubRequest);
	}
	
	private void handleUnsubscribeError(long projectID) {
		logger.error("Failed to unsubscribe from project: " + projectID);
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_UNSUBSCRIBE_REQUEST_ID, IProjectUnsubscribeResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectUnsubscribeResponse p = (IProjectUnsubscribeResponse) e;
			p.unsubscribeFailed(projectID);
		}
	}
	
	private IRequestSendErrorHandler getRequestSendHandler(long projectID) {
		return () -> handleUnsubscribeError(projectID);
	}
}
