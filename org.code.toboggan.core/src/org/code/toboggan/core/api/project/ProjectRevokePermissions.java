package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectRevokePermissionsExtension;

public class ProjectRevokePermissions extends AbstractAPICall {

	private long projectID;
	private String name;

	public ProjectRevokePermissions(AbstractExtensionManager manager, long projectID, String name) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_REVOKE_PERMISSIONS_ID,
				IProjectRevokePermissionsExtension.class);
		this.projectID = projectID;
		this.name = name;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectRevokePermissionsExtension pExt = (IProjectRevokePermissionsExtension) e;
			pExt.permissionRevoked(projectID, name);
		}
	}

	public long getProjectID() {
		return projectID;
	}

	public String getName() {
		return name;
	}
}
