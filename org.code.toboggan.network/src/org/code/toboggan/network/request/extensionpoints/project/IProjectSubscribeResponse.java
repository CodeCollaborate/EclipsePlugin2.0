package org.code.toboggan.network.request.extensionpoints.project;

public interface IProjectSubscribeResponse {
	public void subscribed(long projectID);
	public void subscribeFailed(long projectID);
}
