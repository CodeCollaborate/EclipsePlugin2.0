package org.code.toboggan.core.api.project;

import java.nio.file.Path;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectRenameExtension;

public class ProjectRename extends AbstractAPICall {

	private long projectID;
	private String newName;
	private Path newProjectLocation;
	
	public ProjectRename(AbstractExtensionManager manager, long projectID, String newName, Path newProjectLocation) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_RENAME_ID, IProjectRenameExtension.class);
		this.projectID = projectID;
		this.newName = newName;
		this.newProjectLocation = newProjectLocation;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectRenameExtension pExt = (IProjectRenameExtension) e;
			pExt.projectRenamed(projectID, newName, newProjectLocation);
		}
	}

	public long getProjectID() {
		return projectID;
	}

	public String getNewName() {
		return newName;
	}

}
