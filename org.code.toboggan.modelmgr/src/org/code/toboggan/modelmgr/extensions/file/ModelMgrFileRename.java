package org.code.toboggan.modelmgr.extensions.file;

import java.nio.file.Path;

import org.code.toboggan.filesystem.extensionpoints.file.IFSFileRenameExt;
import org.eclipse.core.resources.IFile;

import clientcore.websocket.models.File;

public class ModelMgrFileRename extends AbstractFileModelMgrHandler implements IFSFileRenameExt {

	@Override
	public void fileRenamed(long fileID, String newName, IFile iFile) {
		File file = ss.getFile(fileID);
		long projectID = file.getProjectID();
		Path newFileLocation = iFile.getLocation().toFile().toPath();
		fc.renameFile(fileID, projectID, newName, newFileLocation);
	}
	
	@Override
	public void renameUndone(long fileID, String undoneName, IFile iFile, Path originalFileLocation,
			Path undoneFileLocation) {
		// Do nothing
	}

	@Override
	public void renameUndoFailed(long fileID, String newName, IFile iFile, Path oldFileLocation, Path newFileLocation) {
		// Do nothing
	}

	@Override
	public void folderCreationFailed(long fileID, String folder) {
		// Do nothing
	}

	@Override
	public void renameFailed(long fileID, String newName) {
		// Do nothing
	}

	@Override
	public void renameFileNotFound(long fileID, String newName) {
		// Do nothing
	}

}
