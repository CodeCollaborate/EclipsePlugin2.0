package org.code.toboggan.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import clientcore.dataMgmt.SessionStorage;

public class CoreActivator implements BundleActivator {

	public static final String PLUGIN_ID = "org.code.toboggan.core";
	
	private static BundleContext context;
	private static SessionStorage storage;

	static BundleContext getContext() {
		return context;
	}
	
	public static SessionStorage getSessionStorage() {
		return storage;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		CoreActivator.context = bundleContext;
		storage = new SessionStorage();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		CoreActivator.context = null;
		storage = null;
	}

}
