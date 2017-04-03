package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectGrantPermissionsExtension;

public class ProjectGrantPermissions extends AbstractAPICall {
	private long projectID;
	private String grantUsername;
	private int permission;

	public ProjectGrantPermissions(AbstractExtensionManager manager, long projectID, String grantUsername,
			int permission) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_GRANT_PERMISSIONS_ID,
				IProjectGrantPermissionsExtension.class);
		this.projectID = projectID;
		this.grantUsername = grantUsername;
		this.permission = permission;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
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
