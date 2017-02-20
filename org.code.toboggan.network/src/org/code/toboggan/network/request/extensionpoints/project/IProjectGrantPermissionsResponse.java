package org.code.toboggan.network.request.extensionpoints.project;

public interface IProjectGrantPermissionsResponse {
	public void permissionGranted(long projectID, String grantUsername, int permissionLevel);
	public void permissionGrantFailed(long projectID, String grantUsername, int permissionLevel);
}
