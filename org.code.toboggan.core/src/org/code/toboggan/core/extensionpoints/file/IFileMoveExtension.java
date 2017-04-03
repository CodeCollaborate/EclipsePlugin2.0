package org.code.toboggan.core.extensionpoints.file;

import java.nio.file.Path;

public interface IFileMoveExtension {
	public void fileMoved(long fileID, Path oldAbsolutePath, Path newAbsolutePath);
}
