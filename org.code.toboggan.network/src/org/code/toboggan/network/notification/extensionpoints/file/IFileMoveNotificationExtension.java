package org.code.toboggan.network.notification.extensionpoints.file;

import java.nio.file.Path;

public interface IFileMoveNotificationExtension {
	public void fileMoved(long fileID, Path newPath);
}
