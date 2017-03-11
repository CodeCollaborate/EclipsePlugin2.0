package org.code.toboggan.core.api.project;

import java.util.List;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectLookupExtension;

public class ProjectLookup extends AbstractAPICall {

	private List<Long> projectIDs;
	
	public ProjectLookup(AbstractExtensionManager manager, List<Long> projectIDs) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_LOOKUP_ID, IProjectLookupExtension.class);
		this.projectIDs = projectIDs;
	}
	
	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectLookupExtension pExt = (IProjectLookupExtension) e;
			pExt.lookupProject(projectIDs);
		}
	}
	
	public List<Long> getProjectIDs() {
		return projectIDs;
	}
}
