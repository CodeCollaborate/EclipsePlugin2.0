package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectGetFilesExtension;

public class ProjectGetFiles extends AbstractAPICall {
	private long projectID;

	public ProjectGetFiles(AbstractExtensionManager manager, long projectID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_GET_FILES_ID, IProjectGetFilesExtension.class);
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
