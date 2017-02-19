package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IProjectUnsubscribeExtension extends ICoreExtension {
	public void unsubscribed(long projectID);
}
