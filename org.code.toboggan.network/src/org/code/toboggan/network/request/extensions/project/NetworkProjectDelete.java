package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectDeleteExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectDeletedResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectDeleteRequest;

public class NetworkProjectDelete implements IProjectDeleteExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectDelete.class);
	
	public NetworkProjectDelete() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void projectDeleted(long projectID) {
		// Make delete request
		Request request = (new ProjectDeleteRequest(projectID)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
            	
				// Trigger extensions
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_DELETE_ID);
				for (ICoreExtension e : extensions) {
					IProjectDeletedResponse p = (IProjectDeletedResponse) e;
					p.projectDeleted(projectID);
				}
				
			} else {
				handleDeletionError(projectID);
			}
		}, getRequestSendHandler(projectID));
		
		wsMgr.sendAuthenticatedRequest(request);
	}
	
	private void handleDeletionError(long projectID) {
		logger.error("Failed to delete project from server");
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_DELETE_ID);
		for (ICoreExtension e : extensions) {
			IProjectDeletedResponse p = (IProjectDeletedResponse) e;
			p.projectDeleteFailed(projectID);
		}
	}

	private IRequestSendErrorHandler getRequestSendHandler(long projectID) {
		return () -> {
			handleDeletionError(projectID);
		};
	}
}
