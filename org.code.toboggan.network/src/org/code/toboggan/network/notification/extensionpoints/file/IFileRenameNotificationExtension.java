package org.code.toboggan.network.notification.extensionpoints.file;

import java.nio.file.Path;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IFileRenameNotificationExtension extends ICoreExtension {
	public void fileRenameNotification(long fileID, Path newPath, String newName);
}
