package org.code.toboggan.core.extension.file;

import java.nio.file.Path;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IFileCreateExtension extends ICoreAPIExtension {
	public void fileCreated(String name, Path workspaceRelativePath, long projectID, byte[] fileBytes);
}
