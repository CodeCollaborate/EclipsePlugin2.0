package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IProjectUnsubscribeExtension extends ICoreAPIExtension {
	public void unsubscribed(long projectID);
}
