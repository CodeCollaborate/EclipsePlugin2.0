package org.code.toboggan.core.api.user;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.user.IUserLoginExtension;

public class UserLogin extends AbstractAPICall {

	private String username;
	private String password;
	
	public UserLogin(ExtensionManager manager, String username, String password) {
		this.extensions = manager.getExtensions(ExtensionIDs.USER_LOGIN_ID);
		this.username = username;
		this.password = password;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IUserLoginExtension pExt = (IUserLoginExtension) e;
			pExt.userLogin(username, password);
		}
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
