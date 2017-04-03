package org.code.toboggan.filesystem.extensionpoints.file;

import java.nio.file.Path;

import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.eclipse.core.resources.IFile;

public interface IFSFileRenameExt extends ICoreExtension {
	public void renameUndone(long fileID, String undoneName, IFile iFile, Path originalFileLocation,
			Path undoneFileLocation);

	public void renameUndoFailed(long fileID, String newName, IFile iFile, Path oldFileLocation, Path newFileLocation);

	public void folderCreationFailed(long fileID, String folder);

	public void fileRenamed(long fileID, String newName, Path fileLocation);

	public void renameFailed(long fileID, String newName);

	public void renameFileNotFound(long fileID, String newName);
}
