package org.code.toboggan.filesystem.extensionpoints.file;

import java.nio.file.Path;

import org.code.toboggan.core.extension.ICoreExtension;
import org.eclipse.core.resources.IFile;

public interface IFSFileMoveExt extends ICoreExtension {
	public void moveUndone(long fileID, IFile iFile, Path originalFileLocation, Path undoneFileLocation);
	public void undoFailed(long fileID, IFile iFile, Path oldFileLocation, Path newFileLocation);
}
