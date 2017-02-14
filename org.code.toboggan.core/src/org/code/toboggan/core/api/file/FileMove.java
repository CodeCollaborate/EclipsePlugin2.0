package org.code.toboggan.core.api.file;

import java.nio.file.Path;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.file.IFileMoveExtension;

public class FileMove extends AbstractAPICall {

	private long fileID;
	private Path newWorkspaceRelativePath;

	public FileMove(ExtensionManager manager, long fileID, Path newWorkspaceRelativePath) {
		this.extensions = manager.getExtensions(ExtensionIDs.FILE_MOVE_ID);
		this.fileID = fileID;
		this.newWorkspaceRelativePath = newWorkspaceRelativePath;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IFileMoveExtension pExt = (IFileMoveExtension) e;
			pExt.fileMoved(fileID, newWorkspaceRelativePath);
		}
	}

	public long getFileID() {
		return fileID;
	}

	public Path getNewWorkspaceRelativePath() {
		return newWorkspaceRelativePath;
	}

}
