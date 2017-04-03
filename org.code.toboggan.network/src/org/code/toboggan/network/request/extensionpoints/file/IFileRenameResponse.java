package org.code.toboggan.network.request.extensionpoints.file;

import java.nio.file.Path;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IFileRenameResponse extends ICoreExtension {
	public void fileRenamed(long fileID, Path newFileLocation, String newName);

	public void fileRenameFailed(long fileID, Path oldFileLocation, Path newFileLocation, String newName);
}
