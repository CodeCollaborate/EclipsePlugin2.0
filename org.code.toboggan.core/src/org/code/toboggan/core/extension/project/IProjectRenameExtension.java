package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IProjectRenameExtension extends ICoreAPIExtension {
	public void projectRenamed(long projectID, String newName);
}
