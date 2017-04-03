package org.code.toboggan.core.extensionpoints.project;

import java.nio.file.Path;

public interface IProjectRenameExtension {
	public void projectRenamed(long projectID, String newName, Path newProjectLocation);
}
