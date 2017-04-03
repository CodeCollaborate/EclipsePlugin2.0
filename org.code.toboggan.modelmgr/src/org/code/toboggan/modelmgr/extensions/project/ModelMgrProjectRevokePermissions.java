package org.code.toboggan.modelmgr.extensions.project;

import org.code.toboggan.network.notification.extensionpoints.project.IProjectRevokePermissionsNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.project.IProjectRevokePermissionsResponse;

public class ModelMgrProjectRevokePermissions extends AbstractProjectModelMgrHandler
		implements IProjectRevokePermissionsResponse, IProjectRevokePermissionsNotificationExtension {
	@Override
	public void permissionsRevoked(long projectID, String username) {
		if (ss.getUsername() != null && ss.getUsername().equals(username)) {
			pc.deleteProject(projectID);
		} else {
			pc.removePermission(projectID, username);
		}
	}

	@Override
	public void permissionsRevokeFailed(long projectID, String username) {
		// Do nothing
	}
}
