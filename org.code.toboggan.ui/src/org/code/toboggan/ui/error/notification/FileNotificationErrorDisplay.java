package org.code.toboggan.ui.error.notification;

import java.nio.file.Path;

import org.code.toboggan.network.notification.extensionpoints.file.IFileCreateNotificationExtension;
import org.code.toboggan.network.notification.extensionpoints.file.IFileDeleteNotificationExtension;
import org.code.toboggan.network.notification.extensionpoints.file.IFileMoveNotificationExtension;
import org.code.toboggan.network.notification.extensionpoints.file.IFileRenameNotificationExtension;

import clientcore.websocket.models.File;

public class FileNotificationErrorDisplay implements IFileCreateNotificationExtension, IFileDeleteNotificationExtension,
		IFileMoveNotificationExtension, IFileRenameNotificationExtension {

	@Override
	public void fileRenameNotification(long fileID, Path newPath, String newName) {
		// Do nothing
	}

	@Override
	public void fileMoveNotification(long fileID, Path newFileLocation) {
		// Do nothing
	}

	@Override
	public void fileDeleteNotification(long deletedId) {
		// Do nothing
	}

	@Override
	public void fileCreateNotification(long projectID, File file) {
		// Do nothing
	}

}
