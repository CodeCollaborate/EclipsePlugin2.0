package org.code.toboggan.core.api;

import java.util.Set;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public abstract class AbstractAPICall {
	protected Set<ICoreAPIExtension> extensions;
	
	abstract public void execute();
}
