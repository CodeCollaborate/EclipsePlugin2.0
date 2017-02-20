package org.code.toboggan.network.request.extensionpoints.user;

import java.util.List;

import clientcore.websocket.models.Project;

public interface IUserProjectsResponse {
	public void projectsRetrieved(List<Project> projects);
	public void userProjectsFailed();
}
