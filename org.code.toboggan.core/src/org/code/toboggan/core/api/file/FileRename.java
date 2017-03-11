package org.code.toboggan.core.api.file;

import java.nio.file.Path;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFileRenameExtension;

public class FileRename extends AbstractAPICall {

	private long fileID;
	private Path oldAbsolutePath;
	private Path newAbsolutePath;
	private String newName;

	public FileRename(AbstractExtensionManager manager, long fileID, Path oldAbsolutePath, Path newWorkspaceRelativePath, String newName) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_RENAME_ID, IFileRenameExtension.class);
		this.fileID = fileID;
		this.oldAbsolutePath = oldAbsolutePath;
		this.newAbsolutePath = newWorkspaceRelativePath;
		this.newName = newName;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IFileRenameExtension pExt = (IFileRenameExtension) e;
			pExt.fileRenamed(fileID, oldAbsolutePath, newAbsolutePath, newName);
		}
	}

	public long getFileID() {
		return fileID;
	}

	public Path getNewWorkspaceRelativePath() {
		return newAbsolutePath;
	}

	public String getNewName() {
		return newName;
	}

}
