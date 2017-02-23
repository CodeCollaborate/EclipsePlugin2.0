package org.code.toboggan.ui.error;

import java.nio.file.Path;

import org.code.toboggan.network.request.extensionpoints.file.*;
import org.code.toboggan.ui.dialogs.MessageDialog;

import clientcore.patching.Patch;

public class FileErrorDisplay implements IFileChangeResponse, IFileCreateResponse, IFileDeleteResponse, IFileMoveResponse,
			IFilePullDiffSendChangesResponse, IFilePullResponse, IFileRenameResponse
{

	@Override
	public void fileRenamed(long fileID, Path newWorkspaceRelativePath, String newName) {
		// Do nothing
	}

	@Override
	public void fileRenameFailed(long fileID, Path newWorkspaceRelativePath, String newName) {
		MessageDialog.createDialog("Failed to rename file to " + newName + " on the server. Please resubscribe to the project.").open();
	}

	@Override
	public void filePulled(byte[] fileBytes) {
		// Do nothing
	}

	@Override
	public void filePullFailed(long fileID) {
		MessageDialog.createDialog("Failed to pull file. Please try again.").open();
	}

	@Override
	public void fileMoved(long fileID, Path newWorkspaceRelativePath) {
		// Do nothing
	}

	@Override
	public void fileMoveFailed(long fileID, Path newWorkspaceRelativePath) {
		MessageDialog.createDialog("Failed to move the file. Please resubscribe to the project.").open();
	}

	@Override
	public void fileDeleted(long fileID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fileDeleteFailed(long fileID) {
		MessageDialog.createDialog("Failed to delete the file on the server. Please resubscribe to the project.").open();
	}

	@Override
	public void fileCreated(long fileID) {
		// Do nothing
	}

	@Override
	public void fileCreateFailed(String fileName, Path absolutePath, long projectID, byte[] fileBytes) {
		MessageDialog.createDialog("Failed to create project on the server. Please try again.").open();
	}

	@Override
	public void fileChanged(long fileID, long fileVersion) {
		// Do nothing
	}

	@Override
	public void fileChangeFailed(long fileID, Patch[] patches) {
		MessageDialog.createDialog("Failed to change file on the server. Please resubscribe to the project.").open();
	}
}
