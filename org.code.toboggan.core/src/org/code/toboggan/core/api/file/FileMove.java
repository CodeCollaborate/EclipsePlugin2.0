package org.code.toboggan.core.api.file;

import java.nio.file.Path;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFileMoveExtension;

public class FileMove extends AbstractAPICall {

	private long fileID;
	private Path oldAbsolutePath;
	private Path newAbsolutePath;

	public FileMove(AbstractExtensionManager manager, long fileID, Path oldAbsolutePath, Path newAbsolutePath) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_MOVE_ID, IFileMoveExtension.class);
		this.fileID = fileID;
		this.oldAbsolutePath = oldAbsolutePath;
		this.newAbsolutePath = newAbsolutePath;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IFileMoveExtension pExt = (IFileMoveExtension) e;
			pExt.fileMoved(fileID, oldAbsolutePath, newAbsolutePath);
		}
	}

	public long getFileID() {
		return fileID;
	}

	public Path getNewWorkspaceRelativePath() {
		return newAbsolutePath;
	}

}
