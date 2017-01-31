package org.code.toboggan.core.api;

import java.util.Set;

import org.code.toboggan.core.extension.ICoreApiExtension;

public abstract class AbstractApiCall {
	protected Set<ICoreApiExtension> extensions;
	
	abstract public void execute();
}
