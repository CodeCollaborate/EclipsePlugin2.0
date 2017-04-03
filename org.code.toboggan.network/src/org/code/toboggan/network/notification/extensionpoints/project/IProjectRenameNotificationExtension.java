package org.code.toboggan.network.notification.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IProjectRenameNotificationExtension extends ICoreExtension {
	public void projectRenameNotification(long projectID, String newName);
}
