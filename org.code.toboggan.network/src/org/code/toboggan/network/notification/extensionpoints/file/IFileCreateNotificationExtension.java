package org.code.toboggan.network.notification.extensionpoints.file;

import clientcore.websocket.models.File;

public interface IFileCreateNotificationExtension {
	public void fileCreateNotification(long projectID, File file);
}
