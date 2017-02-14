package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IProjectGetFilesExtension extends ICoreAPIExtension {
	public void getFiles(long projectID);
}
