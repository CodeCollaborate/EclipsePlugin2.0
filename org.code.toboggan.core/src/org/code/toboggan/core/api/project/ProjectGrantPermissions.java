package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.project.IProjectGrantPermissionsExtension;

public class ProjectGrantPermissions extends AbstractAPICall {

	private long projectID;
	private String grantUsername;
	private int permission;
	
	public ProjectGrantPermissions(ExtensionManager manager, long projectID, String grantUsername, int permission) {
		this.extensions = manager.getExtensions(ExtensionIDs.PROJECT_GRANT_PERMISSIONS_ID);
		this.projectID = projectID;
		this.grantUsername = grantUsername;
		this.permission = permission;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IProjectGrantPermissionsExtension pExt = (IProjectGrantPermissionsExtension) e;
			pExt.permissionGranted(projectID, grantUsername, permission);
		}
	}

	public long getProjectID() {
		return projectID;
	}

	public String getGrantUsername() {
		return grantUsername;
	}

	public int getPermission() {
		return permission;
	}

}
