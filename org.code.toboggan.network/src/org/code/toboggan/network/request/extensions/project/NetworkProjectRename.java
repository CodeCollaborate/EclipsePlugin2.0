package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectRenameExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectRenameResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectRenameRequest;

public class NetworkProjectRename implements IProjectRenameExtension {
	
	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectRename.class);
	
	public NetworkProjectRename() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}

	@Override
	public void projectRenamed(long projectID, String newName) {
		Request renameRequest = (new ProjectRenameRequest(projectID, newName)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.debug("Renamed project: " + projectID + " to name " + newName);
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_RENAME_ID);
				for (ICoreExtension e : extensions) {
					IProjectRenameResponse p = (IProjectRenameResponse) e;
					p.projectRenamed(projectID, newName);
				}
			} else {
				handleProjectRenameFailure(projectID, newName);
			}
		}, getRequestSendHandler(projectID, newName));
		wsMgr.sendAuthenticatedRequest(renameRequest);
	}
	
	private void handleProjectRenameFailure(long projectID, String newName) {
		logger.error("Failed to rename project: " + projectID + " to name " + newName);
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_RENAME_ID);
		for (ICoreExtension e : extensions) {
			IProjectRenameResponse p = (IProjectRenameResponse) e;
			p.projectRenameFailed(projectID, newName);
		}
	}
	
	private IRequestSendErrorHandler getRequestSendHandler(long projectID, String newName) {
		return () -> handleProjectRenameFailure(projectID, newName);
	}
}
