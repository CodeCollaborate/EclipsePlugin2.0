package org.code.toboggan.modelmgr.extensions.project;

import java.nio.file.Path;

import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectRenameExt;
import org.code.toboggan.network.request.extensionpoints.project.IProjectRenameResponse;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import clientcore.websocket.models.File;

public class ModelMgrProjectRename extends AbstractProjectModelMgrHandler
		implements IProjectRenameResponse, IFSProjectRenameExt {

	@Override
	public void projectRenamed(long projectID, String newName, Path newProjectLocation) {
		pc.renameProject(projectID, newName, newProjectLocation);

		for (File f : ss.getProject(projectID).getFiles()) {
			ss.setAbsoluteFilePath(newProjectLocation.resolve(f.getRelativePath().resolve(f.getFilename())), projectID,
					f.getFileID());
		}
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

		for (File f : ss.getProject(projectID).getFiles()) {
			ss.setAbsoluteFilePath(newProjectLocation.resolve(f.getRelativePath().resolve(f.getFilename())), projectID,
					f.getFileID());
		}
	}

	@Override
	public void projectRenamedFailed(long projectID) {
		// Do nothing
	}

}
