package org.code.toboggan.modelmgr.extensions.project;

import java.util.Date;

import org.code.toboggan.network.notification.extensionpoints.project.IProjectGrantPermissionsNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.project.IProjectGrantPermissionsResponse;

import clientcore.websocket.models.Permission;

public class ModelMgrProjectGrantPermissions extends AbstractProjectModelMgrHandler implements IProjectGrantPermissionsResponse, IProjectGrantPermissionsNotificationExtension {

	@Override
	public void permissionGranted(long projectID, String grantUsername, int permissionLevel) {
		Permission permission = new Permission(grantUsername, permissionLevel, ss.getUsername(), new Date().toString());
		pc.addPermission(projectID, grantUsername, permission);
	}

	@Override
	public void permissionGrantFailed(long projectID, String grantUsername, int permissionLevel) {
		// Do nothing
	}

	@Override
	public void permissionsGrantedNotification(long projectID, String grantUsername, int permissionLevel) {
		Permission permission = new Permission(grantUsername, permissionLevel, ss.getUsername(), new Date().toString());
		pc.addPermission(projectID, grantUsername, permission);
	}

}
