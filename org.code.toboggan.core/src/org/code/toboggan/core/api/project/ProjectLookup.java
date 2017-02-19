package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectLookupExtension;

public class ProjectLookup extends AbstractAPICall {

	private long projectID;
	
	public ProjectLookup(AbstractExtensionManager manager, long projectID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_LOOKUP_ID);
		this.projectID = projectID;
	}
	
	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectLookupExtension pExt = (IProjectLookupExtension) e;
			pExt.lookupProject(projectID);
		}
	}
	
	public long getProjectID() {
		return projectID;
	}
}
