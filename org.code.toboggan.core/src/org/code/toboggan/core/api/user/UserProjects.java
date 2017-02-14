package org.code.toboggan.core.api.user;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.user.IUserProjectsExtension;

public class UserProjects extends AbstractAPICall {

	private String username;

	public UserProjects(ExtensionManager manager, String username) {
		this.extensions = manager.getExtensions(ExtensionIDs.USER_PROJECTS_ID);
		this.username = username;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IUserProjectsExtension pExt = (IUserProjectsExtension) e;
			pExt.userProjects(username);
		}
	}

	public String getUsername() {
		return username;
	}

}
