package org.code.toboggan.network.notification.clientcorelisteners.file;

import java.nio.file.Path;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.network.notification.extensionpoints.file.IFileMoveNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.notifications.FileMoveNotification;

public class FileMoveNotificationHandler implements INotificationHandler {

	private AbstractExtensionManager extMgr = NetworkExtensionManager.getInstance();
	private Logger logger = LogManager.getLogger(FileMoveNotificationHandler.class);
	private SessionStorage storage = CoreActivator.getSessionStorage();

	@Override
	public void handleNotification(Notification notification) {
		FileMoveNotification n = (FileMoveNotification) notification.getData();
		logger.info("Received file move notification for " + notification.getResourceID());
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_MOVE_NOTIFICATION_ID,
				IFileMoveNotificationExtension.class);

		File file = storage.getFile(notification.getResourceID());
		Path newAbsolutePath = CoreActivator.getSessionStorage().getProjectLocation(file.getProjectID())
				.resolve(n.newPath).resolve(file.getFilename());

		for (ICoreExtension e : extensions) {
			IFileMoveNotificationExtension p = (IFileMoveNotificationExtension) e;
			p.fileMoveNotification(notification.getResourceID(), newAbsolutePath);
		}
	}
}
