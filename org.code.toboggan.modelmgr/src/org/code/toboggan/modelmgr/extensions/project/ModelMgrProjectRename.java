package org.code.toboggan.modelmgr.extensions.project;

import java.nio.file.Path;
import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectRenameExt;
import org.code.toboggan.network.request.extensionpoints.project.IProjectRenameResponse;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class ModelMgrProjectRename extends AbstractProjectModelMgrHandler implements IProjectRenameResponse, IFSProjectRenameExt {

	@Override
	public void projectRenamed(long projectID, String newName, Path newProjectLocation) {
		pc.renameProject(projectID, newName, newProjectLocation);
	}

	@Override
	public void projectRenameFailed(long projectID, String newName) {
		// Do nothing
	}

	@Override
	public void projectRenamed(long projectID, String newName) {
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(newName);
		Path newProjectLocation = iProject.getLocation().toFile().toPath();
		pc.renameProject(projectID, newName, newProjectLocation);
	}
	
}
