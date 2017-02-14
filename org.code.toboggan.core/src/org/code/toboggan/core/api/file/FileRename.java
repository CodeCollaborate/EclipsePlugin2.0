package org.code.toboggan.core.api.file;

import java.nio.file.Path;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.file.IFileRenameExtension;

public class FileRename extends AbstractAPICall {

	private long fileID;
	private Path newWorkspaceRelativePath;
	private String newName;

	public FileRename(ExtensionManager manager, long fileID, Path newWorkspaceRelativePath, String newName) {
		this.extensions = manager.getExtensions(ExtensionIDs.FILE_RENAME_ID);
		this.fileID = fileID;
		this.newWorkspaceRelativePath = newWorkspaceRelativePath;
		this.newName = newName;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IFileRenameExtension pExt = (IFileRenameExtension) e;
			pExt.fileRenamed(fileID, newWorkspaceRelativePath, newName);
		}
	}

	public long getFileID() {
		return fileID;
	}

	public Path getNewWorkspaceRelativePath() {
		return newWorkspaceRelativePath;
	}

	public String getNewName() {
		return newName;
	}

}
