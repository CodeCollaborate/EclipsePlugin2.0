package org.code.toboggan.network.request.extensions;

import org.apache.logging.log4j.LogManager;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.network.NetworkActivator;

public class NetworkExtensionManager extends AbstractExtensionManager {
	public static NetworkExtensionManager getInstance() {
		String className = NetworkExtensionManager.class.getName();
		if (instances.get(className) == null) {
			synchronized (instances) {
				if (instances.get(className) == null) {
					NetworkExtensionManager em = new NetworkExtensionManager();
					instances.put(className, em);
					em.initExtensions(NetworkActivator.PLUGIN_ID);
				}
			}
		}
		return (NetworkExtensionManager) instances.get(className);
	}

	@Override
	public void reset() {
		synchronized (instances) {
			instances.put(NetworkExtensionManager.class.getName(), null);
		}
	}

	private NetworkExtensionManager() {
		logger = LogManager.getLogger(NetworkExtensionManager.class);
	}

}
