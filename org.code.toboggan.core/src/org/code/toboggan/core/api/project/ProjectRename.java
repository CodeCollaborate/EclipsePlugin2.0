package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.project.IProjectRenameExtension;

public class ProjectRename extends AbstractAPICall {

	private long projectID;
	private String newName;
	
	public ProjectRename(ExtensionManager manager, long projectID, String newName) {
		this.extensions = manager.getExtensions(ExtensionIDs.PROJECT_RENAME_ID);
		this.projectID = projectID;
		this.newName = newName;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
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
