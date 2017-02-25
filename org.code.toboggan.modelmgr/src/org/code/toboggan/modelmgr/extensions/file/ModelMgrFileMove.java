package org.code.toboggan.modelmgr.extensions.file;

import java.nio.file.Path;

import org.code.toboggan.filesystem.extensionpoints.file.IFSFileMoveExt;
import org.eclipse.core.resources.IFile;

import clientcore.websocket.models.File;

public class ModelMgrFileMove extends AbstractFileModelMgrHandler implements IFSFileMoveExt {

	@Override
	public void fileMoved(long fileID, IFile iFile, Path newFileLocation) {
		File file = ss.getFile(fileID);
		long projectID = file.getProjectID();
		Path projectLocation = ss.getProjectLocation(fileID);
		Path newRelativePath = projectLocation.relativize(newFileLocation);
		fc.moveFile(fileID, projectID, newFileLocation, newRelativePath);
	}

	@Override
	public void moveFailed(long fileId, IFile iFile, Path newFileLocation) {
		// Do nothing
	}

	@Override
	public void moveUndone(long fileID, IFile iFile, Path originalFileLocation, Path undoneFileLocation) {
		// Do nothing
	}

	@Override
	public void moveUndoFailed(long fileID, IFile iFile, Path oldFileLocation, Path newFileLocation) {
		// Do nothing
	}

	@Override
	public void folderCreationFailed(long fileID, String string) {
		// Do nothing
	}

	@Override
	public void moveFileNotFound(long fileID) {
		// Do nothing
	}

}
