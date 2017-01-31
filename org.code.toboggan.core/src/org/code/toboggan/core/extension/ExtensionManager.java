package org.code.toboggan.core.extension;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ExtensionManager {
	private static ExtensionManager instance;
	private HashMap<String, HashSet<ICoreApiExtension>> extensions;
	
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
	}
	
	// TODO: Add updateExtensions method
	
	public Set<ICoreApiExtension> getExtensions(String extensionID) {
		return Collections.unmodifiableSet(this.extensions.get(extensionID));
	}
}
