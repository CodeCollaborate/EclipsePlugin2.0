package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IProjectRevokePermissionsExtension extends ICoreAPIExtension {
	public void permissionRevoked(long projectID, String name);
}
