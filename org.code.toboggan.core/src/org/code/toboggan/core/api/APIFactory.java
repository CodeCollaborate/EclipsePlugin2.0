package org.code.toboggan.core.api;

import org.code.toboggan.core.api.project.ProjectCreate;
import org.code.toboggan.core.extension.ExtensionManager;

public class APIFactory {
	
	private static final ExtensionManager EXT_MGR = ExtensionManager.getInstance();
	
	public static ProjectCreate createProjectCreate(String name) {
		return new ProjectCreate(EXT_MGR, name);
	}
}
