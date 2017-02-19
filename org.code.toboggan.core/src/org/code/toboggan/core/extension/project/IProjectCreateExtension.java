package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IProjectCreateExtension extends ICoreExtension {
	public void projectCreated(String name);
}
