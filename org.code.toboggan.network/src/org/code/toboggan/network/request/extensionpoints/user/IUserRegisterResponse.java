package org.code.toboggan.network.request.extensionpoints.user;

public interface IUserRegisterResponse {
	public void userRegistered(String username);
	public void userRegistrationFailed(String username, String firstName, String lastName, String email);
}
