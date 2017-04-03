package org.code.toboggan.core.api;

import java.util.Set;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.ICoreExtension;

public abstract class AbstractAPICall implements Runnable {
	protected Set<ICoreExtension> extensions;

	abstract public void execute();

	public void runAsync() {
		CoreActivator.getExecutor().execute(this);
	}

	@Override
	public void run() {
		execute();
	}
}
