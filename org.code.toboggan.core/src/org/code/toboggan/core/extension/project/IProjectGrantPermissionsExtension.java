package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IProjectGrantPermissionsExtension extends ICoreAPIExtension {
	public void permissionGranted(long projectID, String grantUsername, int permission);
}
