package org.code.toboggan.network.notification.clientcorelisteners.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectRevokePermissionsNotificationExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.INotificationHandler;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.notifications.ProjectRevokePermissionsNotification;

public class ProjectRevokePermissionsNotificationHandler implements INotificationHandler {

	private AbstractExtensionManager extMgr = NetworkExtensionManager.getInstance();
	private Logger logger = LogManager.getLogger(ProjectRevokePermissionsNotificationHandler.class);
	
	@Override
	public void handleNotification(Notification notification) {
		ProjectRevokePermissionsNotification n = (ProjectRevokePermissionsNotification) notification.getData();
		logger.info("Received project revoke permissions notification for " + notification.getResourceID());
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_REVOKE_PERMISSIONS_NOTIFICATION_ID, IProjectRevokePermissionsNotificationExtension.class);
		for (ICoreExtension e : extensions) {
			IProjectRevokePermissionsNotificationExtension p = (IProjectRevokePermissionsNotificationExtension) e;
			p.permissionRevokedNotification(notification.getResourceID(), n.revokeUsername);
		}
	}
}
