package org.code.toboggan.network.notification.extensionpoints.project;

public interface IProjectRenameNotificationExtension {
	public void projectRenamed(long projectID, String newName);
}
