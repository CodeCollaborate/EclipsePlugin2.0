package org.code.toboggan.network.request.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IProjectRevokePermissionsResponse extends ICoreExtension {
	public void permissionsRevoked(long projectID, String username);
	public void permissionsRevokeFailed(long projectID, String username);
}
