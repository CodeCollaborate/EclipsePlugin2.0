package org.code.toboggan.core.extensionpoints.file;

import java.nio.file.Path;

public interface IFileRenameExtension {
	public void fileRenamed(long fileID, Path oldAbsolutePath, Path newWorkspaceRelativePath, String newName);
}
