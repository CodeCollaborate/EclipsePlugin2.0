package org.code.toboggan.core.extensionpoints.file;

import java.nio.file.Path;

public interface IFileCreateExtension {
	public void fileCreated(String name, Path absoluteFilePath, long projectID, byte[] fileBytes);
}
