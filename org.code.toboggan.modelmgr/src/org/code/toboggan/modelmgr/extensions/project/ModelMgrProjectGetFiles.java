package org.code.toboggan.modelmgr.extensions.project;

import java.nio.file.Path;
import java.util.Arrays;

import org.code.toboggan.network.request.extensionpoints.project.IProjectGetFilesResponse;

import clientcore.dataMgmt.FileController;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;

public class ModelMgrProjectGetFiles extends AbstractProjectModelMgrHandler implements IProjectGetFilesResponse {

	@Override
	public void projectGetFiles(long projectID, File[] files) {
		Project project = ss.getProject(projectID);

		// Set the project's files list in metadata
		project.setFiles(Arrays.asList(files));

		// Setup the absolute-filepath to fileMetadata map
		FileController fc = new FileController(ss);
		Path projectLocation = ss.getProjectLocation(projectID);
		for (File fData : files) {
			fc.putFileLocation(projectLocation.resolve(fData.getRelativePath()).resolve(fData.getFilename()),
					fData.getProjectID(), fData.getFileID());
		}
	}

	@Override
	public void projectGetFilesFailed(long projectID) {
		// Do nothing
	}
}
