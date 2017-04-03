package org.code.toboggan.network.request.extensionpoints.project;

import java.util.List;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.websocket.models.Project;

public interface IProjectLookupResponse extends ICoreExtension {
	public void projectFound(List<Project> projects);

	public void projectLookupFailed(List<Long> projectIDs);
}
