package org.code.toboggan.network.request.extensionpoints.project;

import clientcore.websocket.models.File;

public interface IProjectGetFilesResponse {
	public void projectGetFiles(File[] files);
	public void projectGetFilesFailed(long projectID);
}
