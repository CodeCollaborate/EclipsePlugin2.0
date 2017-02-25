package org.code.toboggan.modelmgr.extensions.project;

import java.util.List;

import org.code.toboggan.network.request.extensionpoints.project.IProjectLookupResponse;

import clientcore.websocket.models.Project;

public class ModelMgrProjectLookup extends AbstractProjectModelMgrHandler implements IProjectLookupResponse {

	@Override
	public void projectFound(List<Project> projects) {
		ss.setProjects(projects);
	}

	@Override
	public void projectLookupFailed(List<Long> projectIDs) {
		// Do nothing		
	}

}
