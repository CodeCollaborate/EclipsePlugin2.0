package org.code.toboggan.network.request.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IProjectGrantPermissionsResponse extends ICoreExtension {
	public void permissionGranted(long projectID, String grantUsername, int permissionLevel);

	public void permissionGrantFailed(long projectID, String grantUsername, int permissionLevel);
}
