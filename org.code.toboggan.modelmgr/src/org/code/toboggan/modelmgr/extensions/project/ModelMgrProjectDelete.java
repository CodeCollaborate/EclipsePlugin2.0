package org.code.toboggan.modelmgr.extensions.project;

import org.code.toboggan.network.notification.extensionpoints.project.IProjectDeleteNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.project.IProjectDeleteResponse;

public class ModelMgrProjectDelete extends AbstractProjectModelMgrHandler
		implements IProjectDeleteResponse, IProjectDeleteNotificationExtension {

	@Override
	public void projectDeleted(long projectID) {
		pc.deleteProject(projectID);
	}

	@Override
	public void projectDeleteNotification(long projectID) {
		pc.deleteProject(projectID);
	}

	@Override
	public void projectDeleteFailed(long projectID) {
		// Do nothing
	}
}
