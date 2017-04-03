package org.code.toboggan.network.notification.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IProjectRevokePermissionsNotificationExtension extends ICoreExtension {
	public void permissionsRevoked(long projectID, String username);
}
