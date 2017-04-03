package org.code.toboggan.core.extensionpoints;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.storage.IStorageListener;

public class APIExtensionManager extends AbstractExtensionManager {
	protected Logger logger = LogManager.getLogger(APIExtensionManager.class);

	public static APIExtensionManager getInstance() {
		String className = APIExtensionManager.class.getName();
		if (instances.get(className) == null) {
			synchronized (instances) {
				if (instances.get(className) == null) {
					APIExtensionManager em = new APIExtensionManager();
					instances.put(className, em);
					em.initExtensions(CoreActivator.PLUGIN_ID);
					em.registerPropertyChangeListeners();
				}
			}
		}
		return (APIExtensionManager) instances.get(className);
	}

	@Override
	public void reset() {
		synchronized (instances) {
			instances.put(APIExtensionManager.class.getName(), null);
		}
	}

	private APIExtensionManager() {
		for (String e : this.extensions.keySet()) {
			this.updateExtensions(e);
		}
	}

	public void registerPropertyChangeListeners() {
		CoreActivator.getSessionStorage().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Set<ICoreExtension> storageListeners = getExtensions(APIExtensionIDs.STORAGE_LISTENER_ID,
						IStorageListener.class);
				for (ICoreExtension e : storageListeners) {
					if (e instanceof IStorageListener) {
						IStorageListener storList = (IStorageListener) e;
						storList.propertyChange(evt);
					}
				}
			}
		});
	}
}
