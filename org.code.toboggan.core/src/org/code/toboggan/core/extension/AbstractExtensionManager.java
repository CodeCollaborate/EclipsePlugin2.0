package org.code.toboggan.core.extension;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

public abstract class AbstractExtensionManager {

	protected static Map<String, AbstractExtensionManager> instances = new HashMap<>();
	protected Map<String, Set<ICoreExtension>> extensions = new HashMap<>();
	protected Logger logger;

	public void resetAll() {
		instances = new HashMap<>();
	}
	
	public abstract void reset();
	
	protected void updateExtensions(String extensionID) {
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
					if (o instanceof ICoreExtension) {
						Set<ICoreExtension> registeredExtensions;
						if ((registeredExtensions = this.extensions.get(extensionID)) == null) {
							registeredExtensions = new HashSet<>();
							registeredExtensions.add((ICoreExtension) o);
							this.extensions.put(extensionID, registeredExtensions);
						} else {
							registeredExtensions.add((ICoreExtension) o);
						}
					}
				} catch (Exception e) {
					String message = extension.getLabel() + " [" + extensionID + "]" + " error intializing factory.";
					logger.error(message, new UnsupportedOperationException());
				}
			}
			
		}
	}

	public Set<ICoreExtension> getExtensions(String extensionID) {
		return Collections.unmodifiableSet(this.extensions.get(extensionID));
	}

}
