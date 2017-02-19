package org.code.toboggan.network.request.extensionpoints;

import clientcore.websocket.models.Project;

public interface IProjectCreateResponse {
	public void projectCreated(Project p);
}
