package org.code.toboggan.core.extension.user;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IUserLoginExtension extends ICoreAPIExtension {
	public void userLogin(String username, String password);
}
