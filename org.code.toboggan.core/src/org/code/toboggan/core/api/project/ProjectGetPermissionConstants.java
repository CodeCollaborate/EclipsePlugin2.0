package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.project.IProjectGetPermissionConstantsExtension;

public class ProjectGetPermissionConstants extends AbstractAPICall {

	public ProjectGetPermissionConstants(ExtensionManager manager) {
		this.extensions = manager.getExtensions(ExtensionIDs.PROJECT_GET_PERMISSIONS_CONST_ID);
	}
	
	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IProjectGetPermissionConstantsExtension pExt = (IProjectGetPermissionConstantsExtension) e;
			pExt.getPermissionConstants();
		}
	}

}
