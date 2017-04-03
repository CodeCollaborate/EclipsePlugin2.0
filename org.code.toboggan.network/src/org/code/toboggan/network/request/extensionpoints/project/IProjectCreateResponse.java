package org.code.toboggan.network.request.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.websocket.models.Project;

public interface IProjectCreateResponse extends ICoreExtension {
	public void projectCreated(long projectId);

	public void projectCreationFailed(String name);

	public void subscribed(long projectId);

	public void subscribeFailed(long projectId);

	public void projectFetched(Project p);

	public void projectFetchFailed(long projectId);
}
