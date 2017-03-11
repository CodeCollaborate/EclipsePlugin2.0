package org.code.toboggan.network.notification.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IProjectDeleteNotificationExtension extends ICoreExtension {
	public void projectDeleteNotification(long projectId);
}
