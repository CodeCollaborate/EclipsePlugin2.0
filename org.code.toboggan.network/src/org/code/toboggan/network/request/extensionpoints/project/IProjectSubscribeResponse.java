package org.code.toboggan.network.request.extensionpoints.project;

import java.util.List;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.websocket.models.File;

public interface IProjectSubscribeResponse extends ICoreExtension {
	public void subscribed(long projectID, List<File> files);
	public void subscribeFailed(long projectID);
}
