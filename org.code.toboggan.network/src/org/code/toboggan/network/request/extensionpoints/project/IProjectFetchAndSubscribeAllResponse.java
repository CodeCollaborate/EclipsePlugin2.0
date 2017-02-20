package org.code.toboggan.network.request.extensionpoints.project;

import java.util.List;

import clientcore.websocket.models.Project;

public interface IProjectFetchAndSubscribeAllResponse {
	public void fetchedAll(List<Project> projects);
	public void fetchAllFailed();
	public void subscribed(long subscribeId);
	public void subscribeFailed(long subscribeId);
}
