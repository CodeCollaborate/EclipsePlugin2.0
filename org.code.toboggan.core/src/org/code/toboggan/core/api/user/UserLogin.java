package org.code.toboggan.core.api.user;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.user.IUserLoginExtension;

public class UserLogin extends AbstractAPICall {

	private String username;
	private String password;
	
	public UserLogin(AbstractExtensionManager manager, String username, String password) {
		this.extensions = manager.getExtensions(APIExtensionIDs.USER_LOGIN_ID, IUserLoginExtension.class);
		this.username = username;
		this.password = password;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
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
