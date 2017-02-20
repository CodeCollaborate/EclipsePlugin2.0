package org.code.toboggan.core.extension.project;

import java.util.List;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IProjectLookupExtension extends ICoreExtension {
	public void lookupProject(List<Long> projectIDs);
}
