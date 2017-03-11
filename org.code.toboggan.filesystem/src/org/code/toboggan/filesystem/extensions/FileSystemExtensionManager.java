package org.code.toboggan.filesystem.extensions;

import org.apache.logging.log4j.LogManager;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.filesystem.FSActivator;

public class FileSystemExtensionManager extends AbstractExtensionManager {
	public static FileSystemExtensionManager getInstance() {
		String className = FileSystemExtensionManager.class.getName();
		if (instances.get(className) == null) {
			synchronized (FileSystemExtensionManager.class) {
				if (instances.get(className) == null) {
					FileSystemExtensionManager em = new FileSystemExtensionManager();
					instances.put(className, em);
					em.initExtensions(FSActivator.PLUGIN_ID);
				}
			}
		}
		return (FileSystemExtensionManager) instances.get(className);
	}
	
	@Override
	public void reset() {
		instances.put(FileSystemExtensionManager.class.getName(), null);
	}

	private FileSystemExtensionManager() {
		logger = LogManager.getLogger(FileSystemExtensionManager.class);
	}
	
}
