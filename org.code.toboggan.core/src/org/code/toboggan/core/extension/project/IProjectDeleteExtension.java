package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IProjectDeleteExtension extends ICoreAPIExtension {
	public void projectDeleted(long projectID);
}
