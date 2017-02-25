package org.code.toboggan.network.notification.extensionpoints.project;

public interface IProjectRevokePermissionsNotificationExtension {
	public void permissionRevokedNotification(long projectID, String name);
}
