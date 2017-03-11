package org.code.toboggan.network.notification.clientcorelisteners.file;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.network.notification.extensionpoints.file.IFileDeleteNotificationExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.Notification;

public class FileDeleteNotificationHandler implements INotificationHandler {

	private AbstractExtensionManager extMgr = NetworkExtensionManager.getInstance();
	private Logger logger = LogManager.getLogger(FileDeleteNotificationHandler.class);
	
	@Override
	public void handleNotification(Notification notification) {
		logger.info("Received file delete notification for " + notification.getResourceID());
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_DELETE_NOTIFICATION_ID, IFileDeleteNotificationExtension.class);
		for (ICoreExtension e : extensions) {
			IFileDeleteNotificationExtension p = (IFileDeleteNotificationExtension) e;
			p.fileDeleteNotification(notification.getResourceID());
		}
	}

}
