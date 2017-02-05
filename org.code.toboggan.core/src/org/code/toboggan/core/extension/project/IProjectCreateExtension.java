package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreApiExtension;

import websocket.models.*;

public interface IProjectCreateExtension extends ICoreApiExtension {
	public void projectCreated(Project p);
	public void onLocalSuccess(Project p);
	public void onLocalFailure(Project p);
	public void onRemoteSuccess(Project p);
	public void onRemoteFailure(Project p);
}
