package org.code.toboggan.core.api;

import java.util.Set;

import org.code.toboggan.core.extension.ICoreExtension;

public abstract class AbstractAPICall implements Runnable {
	protected Set<ICoreExtension> extensions;
	
	abstract public void execute();
	
	@Override
	public void run() {
		execute();
	}
}
