package org.code.toboggan.network.notification.extensionpoints.file;

import java.nio.file.Path;

public interface IFileMoveNotificationExtension {
	public void fileMoveNotification(long fileID, Path newFileLocation);
}
