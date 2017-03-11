package org.code.toboggan.network.notification.clientcorelisteners.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectDeleteNotificationExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.Notification;

public class ProjectDeleteNotificationHandler implements INotificationHandler {

	private AbstractExtensionManager extMgr = NetworkExtensionManager.getInstance();
	private Logger logger = LogManager.getLogger(ProjectDeleteNotificationHandler.class);
	
	@Override
	public void handleNotification(Notification notification) {
		logger.info("Received project delete notification for " + notification.getResourceID());
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_DELETE_NOTIFICATION_ID, IProjectDeleteNotificationExtension.class);
		for (ICoreExtension e : extensions) {
			IProjectDeleteNotificationExtension p = (IProjectDeleteNotificationExtension) e;
			p.projectDeleteNotification(notification.getResourceID());
		}
	}

}
