package org.code.toboggan.network.request.extensionpoints.project;

import clientcore.websocket.models.Project;

public interface IProjectCreateResponse {
	public void projectCreated(Project p);
	public void projectCreationFailed(String name);
}
