package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectDeleteExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.project.IProjectDeleteResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectDeleteRequest;

public class NetworkProjectDelete extends AbstractNetworkExtension implements IProjectDeleteExtension {
	private Logger logger = LogManager.getLogger(NetworkProjectDelete.class);

	public NetworkProjectDelete() {
		super();
	}

	@Override
	public void projectDeleted(long projectID) {
		extMgr = NetworkExtensionManager.getInstance();
		// Make delete request
		Request request = (new ProjectDeleteRequest(projectID)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Successfully deleted project " + projectID + " from server");
				// Trigger extensions
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_DELETE_REQUEST_ID,
						IProjectDeleteResponse.class);
				for (ICoreExtension e : extensions) {
					IProjectDeleteResponse p = (IProjectDeleteResponse) e;
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_DELETE_REQUEST_ID,
				IProjectDeleteResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectDeleteResponse p = (IProjectDeleteResponse) e;
			p.projectDeleteFailed(projectID);
		}
	}

	private IRequestSendErrorHandler getRequestSendHandler(long projectID) {
		return () -> {
			handleDeletionError(projectID);
		};
	}
}
