package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectUnsubscribeExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectUnsubscribeResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectUnsubscribeRequest;

public class NetworkProjectUnsubscribe implements IProjectUnsubscribeExtension {
	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectUnsubscribe.class);
	
	public NetworkProjectUnsubscribe() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void unsubscribed(long projectID) {
		Request unsubRequest = (new ProjectUnsubscribeRequest(projectID)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Success unsubscribing from project: " + projectID);
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_UNSUBSCRIBE_ID, IProjectUnsubscribeResponse.class);
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_UNSUBSCRIBE_ID, IProjectUnsubscribeResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectUnsubscribeResponse p = (IProjectUnsubscribeResponse) e;
			p.unsubscribeFailed(projectID);
		}
	}
	
	private IRequestSendErrorHandler getRequestSendHandler(long projectID) {
		return () -> handleUnsubscribeError(projectID);
	}
}
