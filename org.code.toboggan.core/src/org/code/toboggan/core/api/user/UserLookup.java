package org.code.toboggan.core.api.user;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.user.IUserLookupExtension;

public class UserLookup extends AbstractAPICall {

	private String username;

	public UserLookup(ExtensionManager manager, String username) {
		this.extensions = manager.getExtensions(ExtensionIDs.USER_LOOKUP_ID);
		this.username = username;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IUserLookupExtension pExt = (IUserLookupExtension) e;
			pExt.userLookup(username);
		}
	}

	public String getUsername() {
		return username;
	}

}
