package org.code.toboggan.core.extension.file;

import java.nio.file.Path;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IFileMoveExtension extends ICoreExtension {
	public void fileMoved(long fileID, Path oldAbsolutePath, Path newWorkspaceRelativePath);
}
