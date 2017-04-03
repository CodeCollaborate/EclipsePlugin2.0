package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectUnsubscribeExtension;

public class ProjectUnsubscribe extends AbstractAPICall {

	private long projectID;

	public ProjectUnsubscribe(AbstractExtensionManager manager, long projectID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_UNSUBSCRIBE_ID,
				IProjectUnsubscribeExtension.class);
		this.projectID = projectID;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectUnsubscribeExtension pExt = (IProjectUnsubscribeExtension) e;
			pExt.unsubscribed(projectID);
		}
	}

	public long getProjectID() {
		return projectID;
	}
}
