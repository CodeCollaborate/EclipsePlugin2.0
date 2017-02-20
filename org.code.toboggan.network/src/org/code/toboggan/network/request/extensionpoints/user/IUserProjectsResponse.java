package org.code.toboggan.network.request.extensionpoints.user;

import clientcore.websocket.models.Project;

public interface IUserProjectsResponse {
	public void projectsRetrieved(Project[] projects);
	public void userProjectsFailed();
}
