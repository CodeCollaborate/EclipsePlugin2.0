package org.code.toboggan.core.extension.project;

import java.util.List;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IProjectFetchSubscribeAllExtension extends ICoreExtension {
	public void projectFetchSubscribeAllOccurred(List<Long> projectIDs);
}
