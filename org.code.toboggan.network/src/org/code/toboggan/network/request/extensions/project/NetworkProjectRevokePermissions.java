package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectRevokePermissionsExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectRevokePermissionsResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectRevokePermissionsRequest;

public class NetworkProjectRevokePermissions implements IProjectRevokePermissionsExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectRevokePermissions.class);
	
	public NetworkProjectRevokePermissions() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void permissionRevoked(long projectID, String name) {
		Request revokeRequest = (new ProjectRevokePermissionsRequest(projectID, name)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Project permissions revoked for project: " + projectID + " and user " + name);
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_REVOKE_PERMISSIONS_ID, IProjectRevokePermissionsResponse.class);
				for (ICoreExtension e : extensions) {
					IProjectRevokePermissionsResponse p = (IProjectRevokePermissionsResponse) e;
					p.permissionsRevoked(projectID, name);
				}
			} else {
				handleProjectRevokeError(projectID, name);
			}
		}, getRequestSendHandler(projectID, name));
		this.wsMgr.sendAuthenticatedRequest(revokeRequest);
	}
	
	private void handleProjectRevokeError(long projectID, String name) {
		logger.error("Failed to revoke project permissions for project: " + projectID + " and user " + name);
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_REVOKE_PERMISSIONS_ID, IProjectRevokePermissionsResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectRevokePermissionsResponse p = (IProjectRevokePermissionsResponse) e;
			p.permissionsRevokeFailed(projectID, name);
		}
	}
	
	private IRequestSendErrorHandler getRequestSendHandler(long projectID, String name) {
		return () -> handleProjectRevokeError(projectID, name);
	}
}
