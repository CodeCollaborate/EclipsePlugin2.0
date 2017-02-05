package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreApiExtension;

public interface IProjectCreateExtension extends ICoreApiExtension {
	public void projectCreated(String name);
	public void onLocalSuccess(String name);
	public void onLocalFailure(String name);
	public void onRemoteSuccess(String name);
	public void onRemoteFailure(String name);
}
