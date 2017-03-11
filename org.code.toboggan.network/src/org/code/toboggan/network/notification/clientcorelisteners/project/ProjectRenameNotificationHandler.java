package org.code.toboggan.network.notification.clientcorelisteners.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectRenameNotificationExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.notifications.ProjectRenameNotification;

public class ProjectRenameNotificationHandler implements INotificationHandler {
	
	private AbstractExtensionManager extMgr = NetworkExtensionManager.getInstance();
	private Logger logger = LogManager.getLogger(ProjectRenameNotificationHandler.class);
	@Override
	public void handleNotification(Notification notification) {
		ProjectRenameNotification n = (ProjectRenameNotification) notification.getData();
		logger.info("Received project rename notification for " + notification.getResourceID());
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_RENAME_NOTIFICATION_ID, IProjectRenameNotificationExtension.class);
		for (ICoreExtension e : extensions) {
			IProjectRenameNotificationExtension p = (IProjectRenameNotificationExtension) e;
			p.projectRenameNotification(notification.getResourceID(), n.newName);
		}
	}
}
