package org.code.toboggan.modelmgr.extensions.user;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.network.request.extensionpoints.user.IUserProjectsResponse;

import clientcore.websocket.models.Project;

public class ModelMgrUserProjects implements IUserProjectsResponse {
	Logger logger = LogManager.getLogger(ModelMgrUserProjects.class);

	@Override
	public void projectsRetrieved(List<Project> projects) {
		CoreActivator.getSessionStorage().setProjects(projects);
	}

	@Override
	public void userProjectsFailed() {
		// TODO Auto-generated method stub

	}

}
