package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IProjectSubscribeExtension extends ICoreAPIExtension {
	public void subscribed(long projectID);
}
