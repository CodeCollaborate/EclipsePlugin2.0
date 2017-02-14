package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.project.IProjectGetFilesExtension;

public class ProjectGetFiles extends AbstractAPICall {
	private long projectID;

	public ProjectGetFiles(ExtensionManager manager, long projectID) {
		this.extensions = manager.getExtensions(ExtensionIDs.PROJECT_GET_FILES_ID);
		this.projectID = projectID;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IProjectGetFilesExtension pExt = (IProjectGetFilesExtension) e;
			pExt.getFiles(projectID);
		}
	}

	public long getProjectID() {
		return projectID;
	}
}
