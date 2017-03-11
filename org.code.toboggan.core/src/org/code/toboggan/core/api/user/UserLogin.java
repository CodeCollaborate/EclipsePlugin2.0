package org.code.toboggan.core.api.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.user.IUserLoginExtension;

public class UserLogin extends AbstractAPICall {
	private Logger logger = LogManager.getLogger(UserLogin.class);
	
	private String username;
	private String password;
	
	public UserLogin(AbstractExtensionManager manager, String username, String password) {
		this.extensions = manager.getExtensions(APIExtensionIDs.USER_LOGIN_ID, IUserLoginExtension.class);
		this.username = username;
		this.password = password;
	}

	@Override
	public void execute() {
		logger.debug("User.Login API call triggered");
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
