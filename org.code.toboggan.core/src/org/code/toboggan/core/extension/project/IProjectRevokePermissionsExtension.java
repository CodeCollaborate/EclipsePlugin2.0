package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IProjectRevokePermissionsExtension extends ICoreExtension {
	public void permissionRevoked(long projectID, String name);
}
