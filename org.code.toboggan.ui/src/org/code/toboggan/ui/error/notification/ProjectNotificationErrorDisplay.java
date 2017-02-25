package org.code.toboggan.ui.error.notification;

import org.code.toboggan.network.notification.extensionpoints.project.IProjectDeleteNotificationExtension;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectGrantPermissionsNotificationExtension;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectRenameNotificationExtension;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectRevokePermissionsNotificationExtension;

public class ProjectNotificationErrorDisplay
		implements IProjectDeleteNotificationExtension, IProjectGrantPermissionsNotificationExtension,
		IProjectRenameNotificationExtension, IProjectRevokePermissionsNotificationExtension {

	@Override
	public void permissionRevokedNotification(long projectID, String name) {
		// Do nothing
	}

	@Override
	public void projectRenameNotification(long projectID, String newName) {
		// Do nothing
	}

	@Override
	public void permissionsGrantedNotification(long projectID, String grantUsername, int permission) {
		// Do nothing
	}

	@Override
	public void projectDeleteNotification(long projectId) {
		// Do nothing
	}
}
