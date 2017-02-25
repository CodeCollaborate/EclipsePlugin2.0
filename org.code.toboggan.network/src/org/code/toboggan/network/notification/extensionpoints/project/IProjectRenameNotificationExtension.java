package org.code.toboggan.network.notification.extensionpoints.project;

public interface IProjectRenameNotificationExtension {
	public void projectRenameNotification(long projectID, String newName);
}
