package org.code.toboggan.network.request.extensions;

import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.network.NetworkActivator;

import clientcore.websocket.WSManager;

public abstract class AbstractNetworkExtension implements ICoreExtension {
	protected WSManager wsMgr;
	protected AbstractExtensionManager extMgr;

	public AbstractNetworkExtension() {
		this.wsMgr = NetworkActivator.getWSService().getWSManager();
	}
}
