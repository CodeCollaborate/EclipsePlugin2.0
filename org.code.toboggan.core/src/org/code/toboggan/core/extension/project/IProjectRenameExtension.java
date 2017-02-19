package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IProjectRenameExtension extends ICoreExtension {
	public void projectRenamed(long projectID, String newName);
}
