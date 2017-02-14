package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.project.IProjectLookupExtension;

public class ProjectLookup extends AbstractAPICall {

	private long projectID;
	
	public ProjectLookup(ExtensionManager manager, long projectID) {
		this.projectID = projectID;
	}
	
	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IProjectLookupExtension pExt = (IProjectLookupExtension) e;
			pExt.lookupProject(projectID);
		}
	}
	
	public long getProjectID() {
		return projectID;
	}
}
