package org.code.toboggan.network.request.extensionpoints.user;

public interface IUserLoginResponse {
	public void loggedIn(String username);
	public void loginFailed(String username);
}
