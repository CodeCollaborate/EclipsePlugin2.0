package org.code.toboggan.core.extension.project;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IProjectCreateExtension extends ICoreAPIExtension {
	public void projectCreated(String name);
}
