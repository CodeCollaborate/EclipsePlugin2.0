package org.code.toboggan.modelmgr.extensions.file;

import java.nio.file.Path;

import org.code.toboggan.filesystem.extensionpoints.file.IFSFileCreateExt;

import clientcore.websocket.models.File;

public class ModelMgrFileCreate extends AbstractFileModelMgrHandler implements IFSFileCreateExt {
	/**
	 * fileCreated adds the relevant File object, reconstructing the absolute
	 * path from the project's absolute path, and the project-relative filepath
	 * It then adds it into the SessionStore, persisting the data.
	 */
	@Override
	public void fileCreated(long projectID, File file) {
		ss.setFile(projectID, file);

		// put the location, since we're about to pull.
		Path absolutePath = ss.getProjectLocation(projectID)
				.resolve(file.getRelativePath().resolve(file.getFilename()));
		fc.putFileLocation(absolutePath, projectID, file.getFileID());
	}

}
