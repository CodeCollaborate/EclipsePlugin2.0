package org.code.toboggan.network.request.extensionpoints.project;

import java.nio.file.Path;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IProjectRenameResponse extends ICoreExtension {
	public void projectRenamed(long projectID, String newName, Path newProjectLocation);

	public void projectRenameFailed(long projectID, String newName);
}
