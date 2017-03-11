package org.code.toboggan.network.request.extensionpoints.user;

import java.util.List;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.websocket.models.Project;

public interface IUserProjectsResponse extends ICoreExtension {
	public void projectsRetrieved(List<Project> projects);
	public void userProjectsFailed();
}
