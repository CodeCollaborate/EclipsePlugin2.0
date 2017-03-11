package org.code.toboggan.network.notification.extensionpoints.file;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.websocket.models.File;

public interface IFileCreateNotificationExtension extends ICoreExtension {
	public void fileCreateNotification(long projectID, File file);
}
