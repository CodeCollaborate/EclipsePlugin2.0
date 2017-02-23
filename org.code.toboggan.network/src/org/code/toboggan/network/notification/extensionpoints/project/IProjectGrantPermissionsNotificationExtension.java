package org.code.toboggan.network.notification.extensionpoints.project;

public interface IProjectGrantPermissionsNotificationExtension {
	public void permissionsGranted(long projectID, String grantUsername, int permission);
}
