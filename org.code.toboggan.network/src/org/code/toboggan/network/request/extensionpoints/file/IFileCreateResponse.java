package org.code.toboggan.network.request.extensionpoints.file;

import java.nio.file.Path;

import clientcore.websocket.models.File;

public interface IFileCreateResponse {
	public void fileCreated(File f);
	public void fileCreateFailed(String fileName, Path workspaceRelativePath, long projectID, byte[] fileBytes);
}
