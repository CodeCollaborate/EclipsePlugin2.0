package org.code.toboggan.network.notification.clientcorelisteners.file;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.network.notification.extensionpoints.file.IFileCreateNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.notifications.FileCreateNotification;

public class FileCreateNotificationHandler implements INotificationHandler {

	private AbstractExtensionManager extMgr = NetworkExtensionManager.getInstance();
	private Logger logger = LogManager.getLogger(FileCreateNotificationHandler.class);

	@Override
	public void handleNotification(Notification notification) {
		FileCreateNotification n = (FileCreateNotification) notification.getData();
		logger.info("Received file create notification for " + notification.getResourceID());
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_CREATE_NOTIFICATION_ID,
				IFileCreateNotificationExtension.class);
		for (ICoreExtension e : extensions) {
			IFileCreateNotificationExtension p = (IFileCreateNotificationExtension) e;
			p.fileCreateNotification(notification.getResourceID(), n.file);
		}
	}
}
