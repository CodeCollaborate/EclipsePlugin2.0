package org.code.toboggan.core.api;

import org.code.toboggan.core.api.project.ProjectCreate;
import org.code.toboggan.core.extension.APIExtensionManager;

public class APIFactory {
	
	private static final APIExtensionManager EXT_MGR = APIExtensionManager.getInstance();
	
	public static ProjectCreate createProjectCreate(String name) {
		return new ProjectCreate(EXT_MGR, name);
	}
}
