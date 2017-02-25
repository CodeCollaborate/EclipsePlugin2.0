package org.code.toboggan.network.notification.clientcorelisteners.project;

import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_DELETE_ID, IProjectDeleteNotificationExtension.class);
		for (ICoreExtension e : extensions) {
			IProjectDeleteNotificationExtension p = (IProjectDeleteNotificationExtension) e;
			p.projectDeleteNotification(notification.getResourceID());
		}
	}

}
