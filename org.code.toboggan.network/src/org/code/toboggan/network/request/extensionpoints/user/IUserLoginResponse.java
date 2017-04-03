package org.code.toboggan.network.request.extensionpoints.user;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IUserLoginResponse extends ICoreExtension {
	public void loggedIn(String username, String authToken);

	public void loginFailed(String username);
}
