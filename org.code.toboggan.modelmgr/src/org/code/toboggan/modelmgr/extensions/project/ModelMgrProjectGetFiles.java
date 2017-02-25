package org.code.toboggan.modelmgr.extensions.project;

import java.util.Arrays;

import org.code.toboggan.network.request.extensionpoints.project.IProjectGetFilesResponse;

import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;

public class ModelMgrProjectGetFiles extends AbstractProjectModelMgrHandler implements IProjectGetFilesResponse {

	@Override
	public void projectGetFiles(long projectID, File[] files) {
		Project project = ss.getProject(projectID);
		project.setFiles(Arrays.asList(files));
	}

	@Override
	public void projectGetFilesFailed(long projectID) {
		// Do nothing		
	}

}
