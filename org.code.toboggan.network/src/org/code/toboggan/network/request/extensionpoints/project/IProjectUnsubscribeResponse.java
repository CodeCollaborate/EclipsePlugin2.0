package org.code.toboggan.network.request.extensionpoints.project;

public interface IProjectUnsubscribeResponse {
	public void unsubscribed(long projectID);
	public void unsubscribeFailed(long projectID);
}
