package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectGrantPermissionsExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectGrantPermissionsResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectGrantPermissionsRequest;

public class NetworkProjectGrantPermissions implements IProjectGrantPermissionsExtension {
	
	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectGrantPermissions.class);
	
	public NetworkProjectGrantPermissions() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}

	@Override
	public void permissionGranted(long projectID, String grantUsername, int permission) {
		Request grantPermReq = (new ProjectGrantPermissionsRequest(projectID, grantUsername, permission))
				.getRequest(response -> {
					int status = response.getStatus();
					if (status == 200) {
						logger.info("Granted permissions for project: " + projectID + " and user " + grantUsername + " for level " + permission);
						Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_GRANT_PERMISSIONS_ID, IProjectGrantPermissionsResponse.class);
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_GRANT_PERMISSIONS_ID, IProjectGrantPermissionsResponse.class);
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
