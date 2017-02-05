package org.code.toboggan.core.api.project;

import org.code.toboggan.core.api.AbstractApiCall;
import org.code.toboggan.core.data.ProjectController;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreApiExtension;
import org.code.toboggan.core.extension.project.IProjectCreateExtension;

import websocket.models.*;

public class ProjectCreate extends AbstractApiCall {
	
	private String name;
	
	public ProjectCreate(String name) { // will need to determine the full list of params need for this to be functional
		this.extensions = ExtensionManager.getInstance().getExtensions(ExtensionIDs.PROJECT_CREATE_ID);
		this.name = name;
	}


	@Override
	public void execute() {
		// create model
		ProjectController controller = new ProjectController();
		Project p = controller.createProject(-1, name, null);
		
		// notify
		for (ICoreApiExtension e : this.extensions) {
			IProjectCreateExtension pExt = (IProjectCreateExtension) e;
			pExt.projectCreated(p);
		}
	}

	public String getName() {
		return name;
	}
}
