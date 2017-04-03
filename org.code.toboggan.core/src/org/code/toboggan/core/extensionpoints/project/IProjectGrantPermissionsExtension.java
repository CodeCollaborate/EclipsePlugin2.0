package org.code.toboggan.core.extensionpoints.project;

public interface IProjectGrantPermissionsExtension {
	public void permissionGranted(long projectID, String grantUsername, int permission);
}
