package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectSubscribeExtension;

public class ProjectSubscribe extends AbstractAPICall {

	private long projectID;

	public ProjectSubscribe(AbstractExtensionManager manager, long projectID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_SUBSCRIBE_ID, IProjectSubscribeExtension.class);
		this.projectID = projectID;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectSubscribeExtension pExt = (IProjectSubscribeExtension) e;
			pExt.subscribed(projectID);
		}
	}

	public long getProjectID() {
		return projectID;
	}
}
