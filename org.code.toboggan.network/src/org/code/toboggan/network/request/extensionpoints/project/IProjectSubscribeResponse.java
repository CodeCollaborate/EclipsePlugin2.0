package org.code.toboggan.network.request.extensionpoints.project;

import java.util.List;

import clientcore.websocket.models.File;

public interface IProjectSubscribeResponse {
	public void subscribed(long projectID, List<File> files);
	public void subscribeFailed(long projectID);
}
