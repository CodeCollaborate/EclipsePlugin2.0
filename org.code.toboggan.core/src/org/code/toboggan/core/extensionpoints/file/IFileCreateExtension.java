package org.code.toboggan.core.extensionpoints.file;

import java.nio.file.Path;

public interface IFileCreateExtension {
	public void fileCreated(String name, Path workspaceRelativePath, long projectID, byte[] fileBytes);
}
