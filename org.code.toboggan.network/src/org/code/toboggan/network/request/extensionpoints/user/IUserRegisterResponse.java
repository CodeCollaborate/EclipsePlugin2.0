package org.code.toboggan.network.request.extensionpoints.user;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IUserRegisterResponse extends ICoreExtension {
	public void userRegistered(String username);

	public void userRegistrationFailed(String username, String firstName, String lastName, String email);
}
