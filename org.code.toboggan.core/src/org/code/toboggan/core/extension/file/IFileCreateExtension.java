package org.code.toboggan.core.extension.file;

import java.nio.file.Path;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IFileCreateExtension extends ICoreExtension {
	public void fileCreated(String name, Path workspaceRelativePath, long projectID, byte[] fileBytes);
}
