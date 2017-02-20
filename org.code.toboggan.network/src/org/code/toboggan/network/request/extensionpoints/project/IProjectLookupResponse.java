package org.code.toboggan.network.request.extensionpoints.project;

import java.util.List;

import clientcore.websocket.models.Project;

public interface IProjectLookupResponse {
	public void projectFound(List<Project> projects);
	public void projectLookupFailed(List<Long> projectIDs);
}
