package org.code.toboggan.network.notification.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IProjectRevokePermissionsNotificationExtension extends ICoreExtension {
	public void permissionRevokedNotification(long projectID, String name);
}
