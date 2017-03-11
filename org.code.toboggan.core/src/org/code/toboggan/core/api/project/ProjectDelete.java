package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectDeleteExtension;

public class ProjectDelete extends AbstractAPICall {

	private long projectID;
	
	public ProjectDelete(AbstractExtensionManager manager, long projectID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_DELETE_ID, IProjectDeleteExtension.class);
		this.projectID = projectID;
	}
	
	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectDeleteExtension pExt = (IProjectDeleteExtension) e;
			pExt.projectDeleted(projectID);
		}
	}

}
