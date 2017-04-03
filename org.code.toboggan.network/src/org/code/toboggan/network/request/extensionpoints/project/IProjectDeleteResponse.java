package org.code.toboggan.network.request.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IProjectDeleteResponse extends ICoreExtension {
	public void projectDeleted(long projectID);

	public void projectDeleteFailed(long projectID);
}
