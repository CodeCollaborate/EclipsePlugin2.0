package org.code.toboggan.modelmgr.extensions.project;

import java.util.List;

import org.code.toboggan.network.request.extensionpoints.project.IProjectFetchAndSubscribeAllResponse;

import clientcore.websocket.models.Project;

public class ModelMgrProjectFetchAndSubscribeAll extends AbstractProjectModelMgrHandler implements IProjectFetchAndSubscribeAllResponse {
	@Override
	public void fetchedAll(List<Project> projects) {
		ss.setProjects(projects);
	}

	@Override
	public void fetchAllFailed() {
		// do nothing
	}	
}
