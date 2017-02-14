package org.code.toboggan.core.api.user;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.user.IUserRegisterExtension;

public class UserRegister extends AbstractAPICall {

	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	

	public UserRegister(ExtensionManager manager, String username, String firstName, String lastName, String email, String password) {
		this.extensions = manager.getExtensions(ExtensionIDs.USER_REGISTER_ID);
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}


	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IUserRegisterExtension pExt = (IUserRegisterExtension) e;
			pExt.userRegistered(username, firstName, lastName, email, password);
		}
	}

}
