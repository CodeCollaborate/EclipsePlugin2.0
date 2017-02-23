package org.code.toboggan.core.extension;

import org.apache.logging.log4j.LogManager;

public class APIExtensionManager extends AbstractExtensionManager {
	public static APIExtensionManager getInstance() {
		String className = APIExtensionManager.class.getName();
		if (instances.get(className) == null) {
			instances.put(className, new APIExtensionManager());
		}
		return (APIExtensionManager) instances.get(className);
	}
	
	@Override
	public void reset() {
		instances.put(APIExtensionManager.class.getName(), null);
	}
	
	private APIExtensionManager() {
		for (String e : this.extensions.keySet()) {
			this.updateExtensions(e);
		}
		logger = LogManager.getLogger(APIExtensionManager.class); // TODO: breaking logger?
	}	
}
