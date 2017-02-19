package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectRenameExtension;

public class ProjectRename extends AbstractAPICall {

	private long projectID;
	private String newName;
	
	public ProjectRename(AbstractExtensionManager manager, long projectID, String newName) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_RENAME_ID);
		this.projectID = projectID;
		this.newName = newName;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectRenameExtension pExt = (IProjectRenameExtension) e;
			pExt.projectRenamed(projectID, newName);
		}
	}

	public long getProjectID() {
		return projectID;
	}

	public String getNewName() {
		return newName;
	}

}
