package org.code.toboggan.network.request.extensionpoints.project;

import clientcore.websocket.models.Project;

public interface IProjectLookupResponse {
	public void projectFound(Project p);
	public void projectLookupFailed(long projectID);
}
