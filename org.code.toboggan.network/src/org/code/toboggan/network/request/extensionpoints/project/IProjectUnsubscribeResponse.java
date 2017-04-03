package org.code.toboggan.network.request.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IProjectUnsubscribeResponse extends ICoreExtension {
	public void unsubscribed(long projectID);

	public void unsubscribeFailed(long projectID);
}
