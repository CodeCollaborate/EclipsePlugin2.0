package org.code.toboggan.core.api;

import java.util.Set;

import org.code.toboggan.core.extension.ICoreApiExtension;

public abstract class AbstractAPICall {
	protected Set<ICoreApiExtension> extensions;
	
	abstract public void execute();
}
