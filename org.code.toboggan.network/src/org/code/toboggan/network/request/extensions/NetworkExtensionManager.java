package org.code.toboggan.network.request.extensions;

import org.apache.log4j.LogManager;
import org.code.toboggan.core.extension.AbstractExtensionManager;

public class NetworkExtensionManager extends AbstractExtensionManager {
	public static NetworkExtensionManager getInstance() {
		String className = NetworkExtensionManager.class.getName();
		if (instances.get(className) == null) {
			instances.put(className, new NetworkExtensionManager());
		}
		return (NetworkExtensionManager) instances.get(className);
	}
	
	@Override
	public void reset() {
		instances.put(NetworkExtensionManager.class.getName(), null);
	}

	private NetworkExtensionManager() {
		for (String e : this.extensions.keySet()) {
			this.updateExtensions(e);
		}
		logger = LogManager.getLogger(NetworkExtensionManager.class); // TODO: breaking logger?
	}
}
