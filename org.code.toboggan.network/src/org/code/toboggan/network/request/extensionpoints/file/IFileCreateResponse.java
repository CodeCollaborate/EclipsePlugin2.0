package org.code.toboggan.network.request.extensionpoints.file;

import java.nio.file.Path;

public interface IFileCreateResponse {
	public void fileCreated(long fileID);
	public void fileCreateFailed(String fileName, Path absolutePath, long projectID, byte[] fileBytes);
}
