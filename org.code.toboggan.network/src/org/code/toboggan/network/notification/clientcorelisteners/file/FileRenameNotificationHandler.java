package org.code.toboggan.network.notification.clientcorelisteners.file;

import java.nio.file.Path;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.network.notification.extensionpoints.file.IFileRenameNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.notifications.FileRenameNotification;

public class FileRenameNotificationHandler implements INotificationHandler {

	private AbstractExtensionManager extMgr = NetworkExtensionManager.getInstance();
	private Logger logger = LogManager.getLogger(FileRenameNotificationHandler.class);
	private SessionStorage storage = CoreActivator.getSessionStorage();

	@Override
	public void handleNotification(Notification notification) {
		FileRenameNotification n = (FileRenameNotification) notification.getData();
		logger.info("Received file rename notification for " + notification.getResourceID());
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_RENAME_NOTIFICATION_ID,
				IFileRenameNotificationExtension.class);

		File file = storage.getFile(notification.getResourceID());
		Path newAbsolutePath = CoreActivator.getSessionStorage().getProjectLocation(file.getProjectID())
				.resolve(file.getRelativePath()).resolve(n.newName);

		for (ICoreExtension e : extensions) {
			IFileRenameNotificationExtension p = (IFileRenameNotificationExtension) e;
			p.fileRenameNotification(notification.getResourceID(), newAbsolutePath, n.newName);
		}
	}
}
