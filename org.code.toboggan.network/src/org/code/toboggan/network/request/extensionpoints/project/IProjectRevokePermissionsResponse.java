package org.code.toboggan.network.request.extensionpoints.project;

public interface IProjectRevokePermissionsResponse {
	public void permissionsRevoked(long projectID, String username);
	public void permissionsRevokeFailed(long projectID, String username);
}
