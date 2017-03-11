package org.code.toboggan.network.request.extensionpoints.user;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.websocket.models.User;

public interface IUserLookupResponse extends ICoreExtension {
	public void userFound(User u);
	public void userLookupFailed(String username);
}
