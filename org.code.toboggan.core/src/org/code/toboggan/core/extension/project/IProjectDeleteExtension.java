package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IProjectDeleteExtension extends ICoreExtension {
	public void projectDeleted(long projectID);
}
