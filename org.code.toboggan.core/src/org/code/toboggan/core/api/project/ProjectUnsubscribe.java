package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.project.IProjectUnsubscribeExtension;

public class ProjectUnsubscribe extends AbstractAPICall {

	private long projectID;
	
	public ProjectUnsubscribe(ExtensionManager manager, long projectID) {
		this.extensions = manager.getExtensions(ExtensionIDs.PROJECT_UNSUBSCRIBE_ID);
		this.projectID = projectID;
	}
	
	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IProjectUnsubscribeExtension pExt = (IProjectUnsubscribeExtension) e;
			pExt.unsubscribed(projectID);
		}
	}

	public long getProjectID() {
		return projectID;
	}
}
