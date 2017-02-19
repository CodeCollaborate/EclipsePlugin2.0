package org.code.toboggan.core.api.file;

import java.nio.file.Path;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFileMoveExtension;

public class FileMove extends AbstractAPICall {

	private long fileID;
	private Path newWorkspaceRelativePath;

	public FileMove(AbstractExtensionManager manager, long fileID, Path newWorkspaceRelativePath) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_MOVE_ID);
		this.fileID = fileID;
		this.newWorkspaceRelativePath = newWorkspaceRelativePath;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
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
