package org.code.toboggan.filesystem.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.eclipse.core.resources.IProject;

import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;

public interface IFSProjectSubscribeExt extends ICoreExtension {
	public void subscribed(Project project, IProject iProject, File[] files);
}
