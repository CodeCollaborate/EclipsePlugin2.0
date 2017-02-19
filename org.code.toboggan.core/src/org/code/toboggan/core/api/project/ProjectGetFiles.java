package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectGetFilesExtension;

public class ProjectGetFiles extends AbstractAPICall {
	private long projectID;

	public ProjectGetFiles(AbstractExtensionManager manager, long projectID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_GET_FILES_ID);
		this.projectID = projectID;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectGetFilesExtension pExt = (IProjectGetFilesExtension) e;
			pExt.getFiles(projectID);
		}
	}

	public long getProjectID() {
		return projectID;
	}
}
