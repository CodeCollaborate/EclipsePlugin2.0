package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectFetchAllExtension;

public class ProjectFetchAll extends AbstractAPICall {
	
	public ProjectFetchAll(AbstractExtensionManager manager) {
		this.extensions = manager.getExtensions(ExtensionIDs.PROJECT_FETCH_ALL_ID);
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectFetchAllExtension pExt = (IProjectFetchAllExtension) e;
			pExt.projectFetchAllOccurred();
		}
	}
}
