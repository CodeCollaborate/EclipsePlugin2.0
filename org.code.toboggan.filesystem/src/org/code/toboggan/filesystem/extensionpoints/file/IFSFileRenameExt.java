package org.code.toboggan.filesystem.extensionpoints.file;

import java.nio.file.Path;

import org.code.toboggan.core.extension.ICoreExtension;
import org.eclipse.core.resources.IFile;

public interface IFSFileRenameExt extends ICoreExtension {
	public void renameUndone(long fileID, String undoneName, IFile iFile, Path originalFileLocation, Path undoneFileLocation);
	public void undoFailed(long fileID, String newName, IFile iFile, Path oldFileLocation, Path newFileLocation);
}
