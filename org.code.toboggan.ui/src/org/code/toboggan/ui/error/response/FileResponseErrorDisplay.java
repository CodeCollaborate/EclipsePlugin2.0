package org.code.toboggan.ui.error.response;

import java.nio.file.Path;

import org.code.toboggan.network.request.extensionpoints.file.IFileChangeResponse;
import org.code.toboggan.network.request.extensionpoints.file.IFileCreateResponse;
import org.code.toboggan.network.request.extensionpoints.file.IFileDeleteResponse;
import org.code.toboggan.network.request.extensionpoints.file.IFileMoveResponse;
import org.code.toboggan.network.request.extensionpoints.file.IFilePullDiffSendChangesResponse;
import org.code.toboggan.network.request.extensionpoints.file.IFilePullResponse;
import org.code.toboggan.network.request.extensionpoints.file.IFileRenameResponse;
import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import clientcore.patching.Patch;

public class FileResponseErrorDisplay implements IFileChangeResponse, IFileCreateResponse, IFileDeleteResponse,
		IFileMoveResponse, IFilePullDiffSendChangesResponse, IFilePullResponse, IFileRenameResponse {

	@Override
	public void fileRenamed(long fileID, Path newFileLocation, String newName) {
		// Do nothing
	}

	@Override
	public void fileRenameFailed(long fileID, Path oldFileLocation, Path newFileLocation, String newName) {
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog("Failed to rename the file on the server. Please resubscribe to the project.").open());
	}

	@Override
	public void filePulled(long fileID, byte[] fileBytes, String[] changes) {
		// Do nothing
	}

	@Override
	public void filePullFailed(long fileID) {
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog("Failed to pull file from the server. Please resubscribe to the project.").open());
	}

	@Override
	public void fileMoved(long fileID, Path newFileLocation) {
		// Do nothing
	}

	@Override
	public void fileMoveFailed(long fileID, Path oldFileLocation, Path newFileLocation) {
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog("Failed to move the file on the server. Please resubscribe to the project.").open());
	}

	@Override
	public void fileDeleted(long fileID) {
		// Do nothing
	}

	@Override
	public void fileDeleteFailed(long fileID) {
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog("Failed to delete the file on the server. The file has been recreated locally.").open());
	}

	@Override
	public void fileCreated(long fileID, String name, Path absolutePath, String projectRelativePath, long projectID) {
		// Do nothing
	}

	@Override
	public void fileCreateFailed(String fileName, Path absolutePath, long projectID, byte[] fileBytes) {
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog("Failed to create the file on the server. The file has been deleted locally.").open());
	}

	@Override
	public void fileChanged(long fileID, long fileVersion) {
		// Do nothing
	}

	@Override
	public void fileChangeFailed(long fileID, Patch[] patches) {
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog("Failed to change the file on the server. Please resubscribe to the project.").open());
	}

}
