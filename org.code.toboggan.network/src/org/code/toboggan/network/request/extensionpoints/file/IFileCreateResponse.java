package org.code.toboggan.network.request.extensionpoints.file;

import java.nio.file.Path;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IFileCreateResponse extends ICoreExtension {
	public void fileCreated(long fileID, String name, Path absolutePath, String projectRelativePath, long projectID);
	public void fileCreateFailed(String fileName, Path absolutePath, long projectID, byte[] fileBytes);
}
