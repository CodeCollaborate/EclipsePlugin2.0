package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.project.IProjectDeleteExtension;

public class ProjectDelete extends AbstractAPICall {

	private long projectID;
	
	public ProjectDelete(ExtensionManager manager, long projectID) {
		this.extensions = manager.getExtensions(ExtensionIDs.PROJECT_DELETE_ID);
		this.projectID = projectID;
	}
	
	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IProjectDeleteExtension pExt = (IProjectDeleteExtension) e;
			pExt.projectDeleted(projectID);
		}
	}

}
