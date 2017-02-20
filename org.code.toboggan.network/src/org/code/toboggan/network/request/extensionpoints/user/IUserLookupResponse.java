package org.code.toboggan.network.request.extensionpoints.user;

import clientcore.websocket.models.User;

public interface IUserLookupResponse {
	public void userFound(User u);
	public void userLookupFailed(String username);
}
