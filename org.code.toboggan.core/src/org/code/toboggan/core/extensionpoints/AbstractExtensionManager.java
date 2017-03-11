package org.code.toboggan.core.extensionpoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

	public static void resetAll() {
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
						addExtension(extensionID, (ICoreExtension) o);
					}
				} catch (Exception e) {
					String message = extension.getLabel() + " [" + extensionID + "]" + " error intializing factory.";
					logger.error(message, e);
				}
			}

		}
	}

	public void addExtension(String extensionID, ICoreExtension o) {
		Set<ICoreExtension> registeredExtensions;
		if ((registeredExtensions = this.extensions.get(extensionID)) == null) {
			registeredExtensions = new HashSet<>();
			registeredExtensions.add((ICoreExtension) o);
			this.extensions.put(extensionID, registeredExtensions);
		} else {
			registeredExtensions.add((ICoreExtension) o);
		}
	}

	public Set<ICoreExtension> getExtensions(String extensionID, Class<?> type) {
		Set<ICoreExtension> specificExtensions = new HashSet<>();

		if (this.extensions.containsKey(extensionID)) {
			for (ICoreExtension e : this.extensions.get(extensionID)) {
				if (type.isInstance(e)) {
					specificExtensions.add(e);
				}
			}
		}
		return Collections.unmodifiableSet(specificExtensions);
	}

	/**
	 * Looks through the registered namespaces and updates extension points for
	 * those that begin with the given namespace.
	 * 
	 * @param namespace
	 */
	protected void initExtensions(String namespace) {
		// maybe there's a more efficient way to do this?
		List<String> namespaces = new ArrayList<>();
		for (String s : Platform.getExtensionRegistry().getNamespaces()) {
			if (s.startsWith(namespace)) {
				namespaces.add(s);
			}
		}
		
		for (String s : namespaces) {
			IExtensionPoint[] extPts = Platform.getExtensionRegistry().getExtensionPoints(s);
			for (IExtensionPoint e : extPts) {
				updateExtensions(e.getUniqueIdentifier());
			}
		}
	}
}
