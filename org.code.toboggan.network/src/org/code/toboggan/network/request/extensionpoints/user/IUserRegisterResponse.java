package org.code.toboggan.network.request.extensionpoints.user;

public interface IUserRegisterResponse {
	public void userRegistered(String username);
	public void userRegistrationFailed(String username, String password, String email, String firstName, String lastName);
}
