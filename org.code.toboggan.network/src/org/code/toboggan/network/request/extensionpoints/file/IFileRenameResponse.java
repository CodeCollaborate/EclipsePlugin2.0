package org.code.toboggan.network.request.extensionpoints.file;

import java.nio.file.Path;

public interface IFileRenameResponse {
	public void fileRenamed(long fileID, Path newFileLocation, String newName);
	public void fileRenameFailed(long fileID, Path oldFileLocation, Path newFileLocation, String newName);
}
