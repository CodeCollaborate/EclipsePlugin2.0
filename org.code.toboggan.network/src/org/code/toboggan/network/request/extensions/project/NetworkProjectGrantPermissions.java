package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectGrantPermissionsExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.project.IProjectGrantPermissionsResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectGrantPermissionsRequest;

public class NetworkProjectGrantPermissions extends AbstractNetworkExtension
		implements IProjectGrantPermissionsExtension {
	private Logger logger = LogManager.getLogger(NetworkProjectGrantPermissions.class);

	public NetworkProjectGrantPermissions() {
		super();
	}

	@Override
	public void permissionGranted(long projectID, String grantUsername, int permission) {
		extMgr = NetworkExtensionManager.getInstance();
		Request grantPermReq = (new ProjectGrantPermissionsRequest(projectID, grantUsername, permission))
				.getRequest(response -> {
					int status = response.getStatus();
					if (status == 200) {
						logger.info("Granted permissions for project: " + projectID + " and user " + grantUsername
								+ " for level " + permission);
						Set<ICoreExtension> extensions = extMgr.getExtensions(
								NetworkExtensionIDs.PROJECT_GRANT_PERMISSIONS_REQUEST_ID,
								IProjectGrantPermissionsResponse.class);
						for (ICoreExtension e : extensions) {
							IProjectGrantPermissionsResponse p = (IProjectGrantPermissionsResponse) e;
							p.permissionGranted(projectID, grantUsername, permission);
						}
					} else {
						handlePermissionGrantError(projectID, grantUsername, permission);
					}
				}, getRequestSendHandler(projectID, grantUsername, permission));
		this.wsMgr.sendAuthenticatedRequest(grantPermReq);
	}

	private void handlePermissionGrantError(long projectID, String grantUsername, int permission) {
		logger.error("Failed to grant permission on server");
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_GRANT_PERMISSIONS_REQUEST_ID,
				IProjectGrantPermissionsResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectGrantPermissionsResponse p = (IProjectGrantPermissionsResponse) e;
			p.permissionGrantFailed(projectID, grantUsername, permission);
		}
	}

	private IRequestSendErrorHandler getRequestSendHandler(long projectID, String grantUsername, int permission) {
		return () -> {
			handlePermissionGrantError(projectID, grantUsername, permission);
		};
	}
}
