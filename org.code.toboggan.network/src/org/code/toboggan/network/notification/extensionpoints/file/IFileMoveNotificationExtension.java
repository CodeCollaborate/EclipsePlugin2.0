package org.code.toboggan.network.notification.extensionpoints.file;

import java.nio.file.Path;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IFileMoveNotificationExtension extends ICoreExtension {
	public void fileMoveNotification(long fileID, Path newFileLocation);
}
