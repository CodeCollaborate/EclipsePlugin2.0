package org.code.toboggan.ui.error.filesystem;

import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectCreateExt;
import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectRenameExt;
import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectSubscribeExt;
import org.eclipse.core.resources.IProject;

import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;

public class ProjectFilesystemErrorDisplay implements IFSProjectCreateExt, IFSProjectSubscribeExt, IFSProjectRenameExt {

	@Override
	public void subscribed(Project project, IProject iProject, File[] files) {
		// Do nothing
	}

	@Override
	public void projectCreated(Project project, IProject iProject) {
		// Do nothing
	}

	@Override
	public void projectRenamed(long projectID, String newName) {
		// Do nothing
	}

}
