package org.code.toboggan.core.api.project;

import java.util.List;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectFetchSubscribeAllExtension;

public class ProjectFetchAndSubscribeAll extends AbstractAPICall {
	
	private List<Long> projectIDs;
	
	public ProjectFetchAndSubscribeAll(AbstractExtensionManager manager, List<Long> projectIDs) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_FETCH_SUBSCRIBE_ALL_ID, IProjectFetchSubscribeAllExtension.class);
		this.projectIDs = projectIDs;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectFetchSubscribeAllExtension pExt = (IProjectFetchSubscribeAllExtension) e;
			pExt.projectFetchSubscribeAllOccurred(this.projectIDs);
		}
	}
}
