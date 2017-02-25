package org.code.toboggan.modelmgr.extensions.project;

import org.code.toboggan.network.request.extensionpoints.project.IProjectRevokePermissionsResponse;

public class ModelMgrProjectRevokePermissions extends AbstractProjectModelMgrHandler implements IProjectRevokePermissionsResponse {

	@Override
	public void permissionsRevoked(long projectID, String username) {
		pc.removePermission(projectID, username);
	}

	@Override
	public void permissionsRevokeFailed(long projectID, String username) {
		// Do nothing
	}

}
