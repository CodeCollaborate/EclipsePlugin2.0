package org.code.toboggan.network.notification.extensionpoints.file;

import java.nio.file.Path;

public interface IFileRenameNotificationExtension {
	public void fileRenameNotification(long fileID, Path newPath, String newName);
}
