package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreApiExtension;

import websocket.models.*;

public interface IProjectCreateExtension extends ICoreApiExtension {
	public void projectCreated(Project p);
}
