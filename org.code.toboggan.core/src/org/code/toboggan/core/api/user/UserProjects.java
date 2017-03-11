package org.code.toboggan.core.api.user;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.user.IUserProjectsExtension;

public class UserProjects extends AbstractAPICall {

	public UserProjects(AbstractExtensionManager manager) {
		this.extensions = manager.getExtensions(APIExtensionIDs.USER_PROJECTS_ID, IUserProjectsExtension.class);
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IUserProjectsExtension pExt = (IUserProjectsExtension) e;
			pExt.userProjects();
		}
	}
}
