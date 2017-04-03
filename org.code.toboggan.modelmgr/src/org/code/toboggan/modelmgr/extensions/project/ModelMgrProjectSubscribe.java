package org.code.toboggan.modelmgr.extensions.project;

import java.nio.file.Path;
import java.util.Arrays;

import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectSubscribeExt;
import org.eclipse.core.resources.IProject;

import clientcore.dataMgmt.FileController;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;

public class ModelMgrProjectSubscribe extends AbstractProjectModelMgrHandler implements IFSProjectSubscribeExt {

	@Override
	public void subscribed(Project project, IProject iProject, File[] files) {
		pc.putProjectLocation(iProject.getLocation().toFile().toPath(), project.getProjectID());
		ss.setSubscribed(project.getProjectID());

		// Set the project's files list in metadata
		project.setFiles(Arrays.asList(files));

		// Setup the absolute-filepath to fileMetadata map
		FileController fc = new FileController(ss);
		Path projectLocation = ss.getProjectLocation(project.getProjectID());
		for (File fData : files) {
			fc.putFileLocation(
					projectLocation.resolve(fData.getRelativePath().resolve(fData.getFilename())).normalize(),
					fData.getProjectID(), fData.getFileID());
		}
	}

}
