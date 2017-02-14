package org.code.toboggan.core.extension.file;

import java.nio.file.Path;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IFileRenameExtension extends ICoreAPIExtension {
	public void fileRenamed(long fileID, Path newWorkspaceRelativePath, String newName);
}
