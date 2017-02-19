package org.code.toboggan.core.extension.file;

import java.nio.file.Path;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IFileRenameExtension extends ICoreExtension {
	public void fileRenamed(long fileID, Path newWorkspaceRelativePath, String newName);
}
