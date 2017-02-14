package org.code.toboggan.core.extension.file;

import java.nio.file.Path;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IFileMoveExtension extends ICoreAPIExtension {
	public void fileMoved(long fileID, Path newWorkspaceRelativePath);
}
