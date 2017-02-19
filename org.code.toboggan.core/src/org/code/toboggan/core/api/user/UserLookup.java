package org.code.toboggan.core.api.user;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.user.IUserLookupExtension;

public class UserLookup extends AbstractAPICall {

	private String username;

	public UserLookup(AbstractExtensionManager manager, String username) {
		this.extensions = manager.getExtensions(APIExtensionIDs.USER_LOOKUP_ID);
		this.username = username;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IUserLookupExtension pExt = (IUserLookupExtension) e;
			pExt.userLookup(username);
		}
	}

	public String getUsername() {
		return username;
	}

}
