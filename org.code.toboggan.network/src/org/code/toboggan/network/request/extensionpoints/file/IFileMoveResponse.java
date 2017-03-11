package org.code.toboggan.network.request.extensionpoints.file;

import java.nio.file.Path;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IFileMoveResponse extends ICoreExtension {
	public void fileMoved(long fileID, Path newWorkspaceRelativePath);
	public void fileMoveFailed(long fileID, Path oldFileLocation, Path newWorkspaceRelativePath);
}
