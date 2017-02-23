package org.code.toboggan.network.request.extensionpoints.file;

import java.nio.file.Path;

public interface IFileMoveResponse {
	public void fileMoved(long fileID, Path newWorkspaceRelativePath);
	public void fileMoveFailed(long fileID, Path oldFileLocation, Path newWorkspaceRelativePath);
}
