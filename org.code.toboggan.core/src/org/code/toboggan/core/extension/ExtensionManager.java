package org.code.toboggan.core.extension;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

public class ExtensionManager {
	private static ExtensionManager instance;
	private HashMap<String, HashSet<ICoreAPIExtension>> extensions;
	private Logger logger;
	
	public static ExtensionManager getInstance() {
		if (instance == null) {
			instance = new ExtensionManager();
		}
		return instance;
	}
	
	public void reset() {
		instance = null;
	}
	
	private ExtensionManager() {
		this.extensions = new HashMap<>();
		this.extensions.put(ExtensionIDs.PROJECT_CREATE_ID, new HashSet<>());
		logger = LogManager.getLogger("ExtensionManager");
	}
	
	// TODO: Add updateExtensions method
	private <T> void updateExtensions(String extensionID) {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(extensionID);
		if (point == null) {
			return;
		}
		IExtension[] extensions = point.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] extensionClasses = extension.getConfigurationElements();
			
			for (IConfigurationElement c : extensionClasses) {
				try {
					Object o = c.createExecutableExtension("class");
					if (o instanceof ICoreAPIExtension) {
						this.extensions.get(extensionID).add((ICoreAPIExtension) o);
					}
				} catch (Exception e) {
					String message = extension.getLabel() + " [" + extensionID + "]" + " error intializing factory.";
					logger.error(message, new UnsupportedOperationException());
				}
			}
			
		}
	}
	
	public Set<ICoreAPIExtension> getExtensions(String extensionID) {
		return Collections.unmodifiableSet(this.extensions.get(extensionID));
	}
}
