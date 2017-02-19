package org.code.toboggan.core.api.user;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.user.IUserProjectsExtension;

public class UserProjects extends AbstractAPICall {

	private String username;

	public UserProjects(AbstractExtensionManager manager, String username) {
		this.extensions = manager.getExtensions(APIExtensionIDs.USER_PROJECTS_ID);
		this.username = username;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IUserProjectsExtension pExt = (IUserProjectsExtension) e;
			pExt.userProjects(username);
		}
	}

	public String getUsername() {
		return username;
	}

}
