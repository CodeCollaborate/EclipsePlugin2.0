package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreApiExtension;
import org.code.toboggan.core.extension.project.IProjectCreateExtension;

public class ProjectCreate extends AbstractAPICall {
	
	private String name;
	
	public ProjectCreate(ExtensionManager manager, String name) {
		this.extensions = manager.getExtensions(ExtensionIDs.PROJECT_CREATE_ID);
		this.name = name;
	}

	@Override
	public void execute() {		
		// notify
		for (ICoreApiExtension e : this.extensions) {
			IProjectCreateExtension pExt = (IProjectCreateExtension) e;
			pExt.projectCreated(name);
		}
	}

	public String getName() {
		return name;
	}
}
