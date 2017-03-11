package org.code.toboggan.network.notification.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IProjectGrantPermissionsNotificationExtension extends ICoreExtension {
	public void permissionsGrantedNotification(long projectID, String grantUsername, int permission);
}
