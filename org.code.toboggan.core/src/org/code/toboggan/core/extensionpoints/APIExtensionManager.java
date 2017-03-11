package org.code.toboggan.core.extensionpoints;

import org.apache.logging.log4j.LogManager;
import org.code.toboggan.core.CoreActivator;

public class APIExtensionManager extends AbstractExtensionManager {
	public static APIExtensionManager getInstance() {
		String className = APIExtensionManager.class.getName();
		if (instances.get(className) == null) { 
			synchronized(APIExtensionManager.class) {
				if (instances.get(className) == null) {
					APIExtensionManager em = new APIExtensionManager();
					instances.put(className, em);
					em.initExtensions(CoreActivator.PLUGIN_ID);
				}
			}
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
		logger = LogManager.getLogger(APIExtensionManager.class);
	}
}
