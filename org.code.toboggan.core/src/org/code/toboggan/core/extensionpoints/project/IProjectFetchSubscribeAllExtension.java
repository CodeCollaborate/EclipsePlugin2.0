package org.code.toboggan.core.extensionpoints.project;

import java.util.List;

public interface IProjectFetchSubscribeAllExtension {
	public void projectFetchSubscribeAllOccurred(List<Long> subscribedIds);
}
