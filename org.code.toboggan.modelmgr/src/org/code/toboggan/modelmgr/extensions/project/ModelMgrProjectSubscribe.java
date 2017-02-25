package org.code.toboggan.modelmgr.extensions.project;


import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectSubscribeExt;
import org.eclipse.core.resources.IProject;

import clientcore.websocket.models.Project;

public class ModelMgrProjectSubscribe extends AbstractProjectModelMgrHandler implements IFSProjectSubscribeExt {

	@Override
	public void subscribed(Project project, IProject iProject) {
		ss.setSubscribed(project.getProjectID());
	}

}
