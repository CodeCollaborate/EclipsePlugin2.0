package org.code.toboggan.modelmgr.extensions.project;

import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectCreateExt;
import org.eclipse.core.resources.IProject;

import clientcore.websocket.models.Project;

public class ModelMgrProjectCreate extends AbstractProjectModelMgrHandler implements IFSProjectCreateExt {

	@Override
	public void projectCreated(Project project, IProject iProject) {
		pc.createProject(project);
		pc.putProjectLocation(iProject.getLocation().toFile().toPath(), project.getProjectID());
	}

}
