package org.code.toboggan.core.extension.user;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IUserLoginExtension extends ICoreExtension {
	public void userLogin(String username, String password);
}
