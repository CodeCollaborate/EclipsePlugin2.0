package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IProjectSubscribeExtension extends ICoreExtension {
	public void subscribed(long projectID);
}
