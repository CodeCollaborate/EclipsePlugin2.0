package org.code.toboggan.core.extensionpoints.project;

public interface IProjectRevokePermissionsExtension {
	public void permissionRevoked(long projectID, String name);
}
