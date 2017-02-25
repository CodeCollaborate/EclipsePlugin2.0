package org.code.toboggan.network.notification.clientcorelisteners.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectGrantPermissionsNotificationExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.notifications.ProjectGrantPermissionsNotification;

public class ProjectGrantPermissionsNotificationHandler implements INotificationHandler {
	
	private AbstractExtensionManager extMgr = NetworkExtensionManager.getInstance();
	private Logger logger = LogManager.getLogger(ProjectGrantPermissionsNotificationHandler.class);

	@Override
	public void handleNotification(Notification notification) {
		ProjectGrantPermissionsNotification n = (ProjectGrantPermissionsNotification) notification.getData();
		logger.info("Received project grant permissions notification for " + notification.getResourceID());
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_GRANT_PERMISSIONS_ID);
		for (ICoreExtension e : extensions) {
			IProjectGrantPermissionsNotificationExtension p = (IProjectGrantPermissionsNotificationExtension) e;
			p.permissionsGrantedNotification(notification.getResourceID(), n.grantUsername, n.permissionLevel);
		}
	}
}
