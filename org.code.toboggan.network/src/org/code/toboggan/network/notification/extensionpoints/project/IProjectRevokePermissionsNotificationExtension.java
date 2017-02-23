package org.code.toboggan.network.notification.extensionpoints.project;

public interface IProjectRevokePermissionsNotificationExtension {
	public void permissionRevoked(long projectID, String name);
}
