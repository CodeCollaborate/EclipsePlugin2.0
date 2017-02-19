package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IProjectGrantPermissionsExtension extends ICoreExtension {
	public void permissionGranted(long projectID, String grantUsername, int permission);
}
