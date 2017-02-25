package org.code.toboggan.ui.error.filesystem;

import java.nio.file.Path;

import org.code.toboggan.filesystem.extensionpoints.file.*;
import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.core.resources.IFile;

import clientcore.websocket.models.File;

public class FileFilesystemErrorDisplay implements IFSFileMoveExt, IFSFilePullExt, IFSFileRenameExt {

	@Override
	public void renameUndone(long fileID, String undoneName, IFile iFile, Path originalFileLocation,
			Path undoneFileLocation) {
		MessageDialog.createDialog("Failed to rename the file locally. Please resubscribe to the project.").open();
	}

	@Override
	public void filePulled(File file, IFile iFile) {
		// Do nothing
	}

	@Override
	public void moveUndone(long fileID, IFile iFile, Path originalFileLocation, Path undoneFileLocation) {
		MessageDialog.createDialog("Failed to move the file locally. Please resubscribe to the project.").open();
	}

	@Override
	public void renameUndoFailed(long fileID, String newName, IFile iFile, Path oldFileLocation, Path newFileLocation) {
		MessageDialog.createDialog("Failed to undo a rename operation. Please resubscribe to the project.").open();
	}

	@Override
	public void fileRenamed(long fileID, String newName, IFile iFile) {
		// Do nothing
	}

	@Override
	public void renameFailed(long fileID, String newName) {
		MessageDialog.createDialog("Failed to rename file locally. Please resubscribe ot the project.").open();
	}

	@Override
	public void renameFileNotFound(long fileID, String newName) {
		MessageDialog.createDialog("Could not find the file to be renamed. Please resubscribe to the project.").open();
	}

	@Override
	public void fileMoved(long fileID, IFile iFile, Path newFileLocation) {
		// Do nothing
	}

	@Override
	public void moveFailed(long fileId, IFile iFile, Path newFileLocation) {
		MessageDialog.createDialog("Could not move the file locally. Please resubscribe to the project.").open();
	}

	@Override
	public void moveUndoFailed(long fileID, IFile iFile, Path oldFileLocation, Path newFileLocation) {
		MessageDialog.createDialog("Failed to undo a move operation. Please resubscribe to the project.").open();
	}

	@Override
	public void folderCreationFailed(long fileID, String string) {
		MessageDialog.createDialog("Failed to create required directories. Please resubscribe to the project.").open();
	}

	@Override
	public void moveFileNotFound(long fileID) {
		MessageDialog.createDialog("Could not find the file to be moved. Please resubscribe to the project.").open();
	}
}
