package org.code.toboggan.modelmgr.extensions.project;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectGrantPermissionsNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.project.IProjectGrantPermissionsResponse;

import clientcore.patching.Patch;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Permission;
import clientcore.websocket.models.Project;

public class ModelMgrProjectGrantPermissions extends AbstractProjectModelMgrHandler
		implements IProjectGrantPermissionsResponse, IProjectGrantPermissionsNotificationExtension {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Override
	public void permissionGranted(long projectID, String grantUsername, int permissionLevel) {
		// The below permission that is generated does not have a strictly
		// correct timestamp, but it's close enough.
		Permission permission = new Permission(grantUsername, permissionLevel, ss.getUsername(), new Date().toString());
		pc.addPermission(projectID, grantUsername, permission);
	}

	@Override
	public void permissionGrantFailed(long projectID, String grantUsername, int permissionLevel) {
		// Do nothing
	}

	@Override
	public void permissionsGrantedNotification(long projectID, String grantUsername, int permissionLevel) {
		if (ss.getProject(projectID) == null) {
			logger.debug("Granted permission for a new project - pulling user's projects again");
			APIFactory.createUserProjects().runAsync();
		} else {
			logger.debug("Granted permission for an existing project - updating permission map");
			Permission permission = new Permission(grantUsername, permissionLevel, ss.getUsername(),
					new Date().toString());
			pc.addPermission(projectID, grantUsername, permission);

			// If it was changing our permission to anything but a read, attempt
			// to send changes for all files
			logger.debug("Our permission was changed; attempting to send all local changes");
			if (grantUsername.equalsIgnoreCase(ss.getUsername()) && ss.getPermissionConstants().get("read") != null
					&& permissionLevel > ss.getPermissionConstants().get("read")) {
				Project p = ss.getProject(projectID);
				for(File f : p.getFiles()){
					APIFactory.createFileChange(f.getFileID(), new Patch[]{}, "").runAsync();
				}
			}
		}
	}
}
