package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.project.IProjectRevokePermissionsExtension;

public class ProjectRevokePermissions extends AbstractAPICall {

	private long projectID;
	private String name;
	
	public ProjectRevokePermissions(ExtensionManager manager, long projectID, String name) {
		this.extensions = manager.getExtensions(ExtensionIDs.PROJECT_REVOKE_PERMISSIONS_ID);
		this.projectID = projectID;
		this.name = name;
	}
	
	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
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
