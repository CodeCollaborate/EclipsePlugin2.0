package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.project.IProjectSubscribeExtension;

public class ProjectSubscribe extends AbstractAPICall {

	private long projectID;
	
	public ProjectSubscribe(ExtensionManager manager, long projectID) {
		this.extensions = manager.getExtensions(ExtensionIDs.PROJECT_SUBSCRIBE_ID);
	}
	
	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IProjectSubscribeExtension pExt = (IProjectSubscribeExtension) e;
			pExt.subscribed(projectID);
		}
	}

	public long getProjectID() {
		return projectID;
	}
}
