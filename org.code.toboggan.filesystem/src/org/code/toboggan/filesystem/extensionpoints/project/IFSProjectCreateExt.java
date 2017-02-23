package org.code.toboggan.filesystem.extensionpoints.project;

import org.code.toboggan.core.extension.ICoreExtension;
import org.eclipse.core.resources.IProject;

import clientcore.websocket.models.Project;

public interface IFSProjectCreateExt extends ICoreExtension {
	public void projectCreated(Project project, IProject iProject);
}
