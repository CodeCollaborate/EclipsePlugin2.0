package org.code.toboggan.filesystem.extensionpoints.file;

import java.nio.file.Path;

import org.code.toboggan.core.extension.ICoreExtension;
import org.eclipse.core.resources.IFile;

public interface IFSFileMoveExt extends ICoreExtension {
	public void fileMoved(long fileID, IFile iFile, Path newFileLocation);
	public void moveFailed(long fileId, IFile iFile, Path newFileLocation);
	public void moveUndone(long fileID, IFile iFile, Path originalFileLocation, Path undoneFileLocation);
	public void moveUndoFailed(long fileID, IFile iFile, Path oldFileLocation, Path newFileLocation);
	public void folderCreationFailed(long fileID, String string);
	public void moveFileNotFound(long fileID);
}
