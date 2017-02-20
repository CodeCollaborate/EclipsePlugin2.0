package org.code.toboggan.network.request.extensionpoints.project;

public interface IProjectRenameResponse {
	public void projectRenamed(long projectID, String newName);
	public void projectRenameFailed(long projectID, String newName);
}
