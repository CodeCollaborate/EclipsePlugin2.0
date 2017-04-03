package org.code.toboggan.network.request.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.websocket.models.File;

public interface IProjectGetFilesResponse extends ICoreExtension {
	public void projectGetFiles(long projectID, File[] files);

	public void projectGetFilesFailed(long projectID);
}
