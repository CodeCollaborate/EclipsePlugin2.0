package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectDeleteExtension;

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
