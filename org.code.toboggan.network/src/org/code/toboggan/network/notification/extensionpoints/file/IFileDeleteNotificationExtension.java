package org.code.toboggan.network.notification.extensionpoints.file;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IFileDeleteNotificationExtension extends ICoreExtension {
	public void fileDeleteNotification(long deletedId);
}
