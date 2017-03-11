package org.code.toboggan.core.extensionpoints.user;

public interface IUserRegisterExtension {
	public void userRegistered(String username, String firstName, String lastName, String email, String password);
}
