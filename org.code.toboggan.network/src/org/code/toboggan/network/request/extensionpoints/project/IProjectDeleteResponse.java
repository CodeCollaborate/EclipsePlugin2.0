package org.code.toboggan.network.request.extensionpoints.project;

public interface IProjectDeleteResponse {
	public void projectDeleted(long projectID);
	public void projectDeleteFailed(long projectID);
}
