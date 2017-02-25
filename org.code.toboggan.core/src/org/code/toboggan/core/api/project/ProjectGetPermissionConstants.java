package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectGetPermissionConstantsExtension;

public class ProjectGetPermissionConstants extends AbstractAPICall {

	public ProjectGetPermissionConstants(AbstractExtensionManager manager) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_GET_PERMISSIONS_CONST_ID, IProjectGetPermissionConstantsExtension.class);
	}
	
	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IProjectGetPermissionConstantsExtension pExt = (IProjectGetPermissionConstantsExtension) e;
			pExt.getPermissionConstants();
		}
	}

}
