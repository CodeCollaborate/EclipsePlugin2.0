package org.code.toboggan.network.request.extensionpoints.project;

import java.nio.file.Path;

public interface IProjectRenameResponse {
	public void projectRenamed(long projectID, String newName, Path newProjectLocation);
	public void projectRenameFailed(long projectID, String newName);
}
