package org.code.toboggan.network.notification.clientcorelisteners.file;

import java.nio.file.Path;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.network.notification.extensionpoints.file.IFileRenameNotificationExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.utils.NetworkUtils;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.INotificationHandler;
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_RENAME_ID, IFileRenameNotificationExtension.class);
		for (ICoreExtension e : extensions) {
			IFileRenameNotificationExtension p = (IFileRenameNotificationExtension) e;
			Path oldPath = storage.getFile(notification.getResourceID()).getRelativePath();
			p.fileRenameNotification(notification.getResourceID(), NetworkUtils.toAbsolutePathFromRelative(oldPath, n.newName), n.newName);
		}
	}
}
