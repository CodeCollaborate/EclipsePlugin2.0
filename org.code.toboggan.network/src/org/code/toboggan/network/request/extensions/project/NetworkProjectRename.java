package org.code.toboggan.network.request.extensions.project;

import java.nio.file.Path;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectRenameExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.project.IProjectRenameResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectRenameRequest;

public class NetworkProjectRename extends AbstractNetworkExtension implements IProjectRenameExtension {
	private Logger logger = LogManager.getLogger(NetworkProjectRename.class);
	
	public NetworkProjectRename() {
		super();
	}

	@Override
	public void projectRenamed(long projectID, String newName, Path newProjectLocation) {
		extMgr = NetworkExtensionManager.getInstance();
		Request renameRequest = (new ProjectRenameRequest(projectID, newName)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.debug("Renamed project: " + projectID + " to name " + newName);
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_RENAME_REQUEST_ID, IProjectRenameResponse.class);
				for (ICoreExtension e : extensions) {
					IProjectRenameResponse p = (IProjectRenameResponse) e;
					p.projectRenamed(projectID, newName, newProjectLocation);
				}
			} else {
				handleProjectRenameFailure(projectID, newName);
			}
		}, getRequestSendHandler(projectID, newName));
		wsMgr.sendAuthenticatedRequest(renameRequest);
	}
	
	private void handleProjectRenameFailure(long projectID, String newName) {
		logger.error("Failed to rename project: " + projectID + " to name " + newName);
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_RENAME_REQUEST_ID, IProjectRenameResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectRenameResponse p = (IProjectRenameResponse) e;
			p.projectRenameFailed(projectID, newName);
		}
	}
	
	private IRequestSendErrorHandler getRequestSendHandler(long projectID, String newName) {
		return () -> handleProjectRenameFailure(projectID, newName);
	}
}
