package org.code.toboggan.network.request.extensionpoints.project;

import java.util.List;

import clientcore.websocket.models.Project;

public interface IProjectFetchAllResponse {
	public void fetchedAll(List<Project> projects);
	public void fetchAllFailed();
}
