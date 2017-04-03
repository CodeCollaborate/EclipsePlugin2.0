package org.code.toboggan.network.request.extensionpoints.project;

import java.util.List;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.websocket.models.Project;

public interface IProjectFetchAndSubscribeAllResponse extends ICoreExtension {
	public void fetchedAll(List<Project> projects);

	public void fetchAllFailed();
}
