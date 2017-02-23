package org.code.toboggan.filesystem.extensions;

import org.apache.logging.log4j.LogManager;
import org.code.toboggan.core.extension.AbstractExtensionManager;

public class FileSystemExtensionManager extends AbstractExtensionManager {
	public static FileSystemExtensionManager getInstance() {
		String className = FileSystemExtensionManager.class.getName();
		if (instances.get(className) == null) {
			instances.put(className, new FileSystemExtensionManager());
		}
		return (FileSystemExtensionManager) instances.get(className);
	}
	
	@Override
	public void reset() {
		instances.put(FileSystemExtensionManager.class.getName(), null);
	}

	private FileSystemExtensionManager() {
		for (String e : this.extensions.keySet()) {
			this.updateExtensions(e);
		}
		logger = LogManager.getLogger(FileSystemExtensionManager.class); // TODO: breaking logger?
	}
}
