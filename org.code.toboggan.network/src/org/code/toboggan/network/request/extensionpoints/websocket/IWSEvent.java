package org.code.toboggan.network.request.extensionpoints.websocket;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IWSEvent extends ICoreExtension {
	public void onConnect();
	public void onClose();
	public void onError();
}
