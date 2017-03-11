package org.code.toboggan.filesystem.extensionpoints.file;

import java.nio.file.Path;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IFSFileDeleteExt extends ICoreExtension {
	public void fileOpenInEditor(long fileID, Path fileLocation);
	public void fileDeleted(long fileID);
	public void deleteFailed(long fileID, Path fileLocation);
}
