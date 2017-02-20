package org.code.toboggan.network.request.extensionpoints.project;

public interface IProjectDeletedResponse {
	public void projectDeleted(long projectID);
	public void projectDeleteFailed(long projectID);
}
