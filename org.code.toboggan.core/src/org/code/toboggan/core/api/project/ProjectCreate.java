package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectCreateExtension;

public class ProjectCreate extends AbstractAPICall {
	
	private String name;
	
	public ProjectCreate(AbstractExtensionManager manager, String name) {
		this.extensions = manager.getExtensions(APIExtensionIDs.PROJECT_CREATE_ID);
		this.name = name;
	}

	@Override
	public void execute() {
		// notify
		for (ICoreExtension e : this.extensions) {
			IProjectCreateExtension pExt = (IProjectCreateExtension) e;
			pExt.projectCreated(name);
		}
	}

	public String getName() {
		return name;
	}
}
