package org.code.toboggan.modelmgr.extensions.file;

import java.nio.file.Path;
import java.util.Date;

import org.code.toboggan.network.request.extensionpoints.file.IFileCreateResponse;

import clientcore.websocket.models.File;

public class ModelMgrFileCreate extends AbstractFileModelMgrHandler implements IFileCreateResponse {
	
	@Override
	public void fileCreated(long fileID, String name, Path absolutePath, String projectRelativePath, long projectID) {
		File f = new File(fileID, name, projectRelativePath, 1, ss.getUsername(), new Date().toString()); // doing date like this is questionable, but it's not being used
		ss.setFile(projectID, f);
		// put the location since this client created it
		fc.putFileLocation(absolutePath, projectID, fileID);
	}

	@Override
	public void fileCreateFailed(String fileName, Path absolutePath, long projectID, byte[] fileBytes) {
		// Do nothing
	}

}
