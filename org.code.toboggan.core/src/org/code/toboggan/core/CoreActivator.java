package org.code.toboggan.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import clientcore.dataMgmt.SessionStorage;

public class CoreActivator extends Plugin {

	public static final String PLUGIN_ID = "org.code.toboggan.core";
	
	private static BundleContext context;
	private static SessionStorage storage;
	private static ExecutorService executor;

	static BundleContext getContext() {
		return context;
	}
	
	public static SessionStorage getSessionStorage() {
		return storage;
	}
	
	public static void reset() {
		storage = new SessionStorage();
		executor = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
	}
	
	public static ExecutorService getExecutor() {
		return executor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		CoreActivator.context = bundleContext;
		
		String pluginStateLocation = getStateLocation().toString();
		System.setProperty("logPath", pluginStateLocation);
		
		reset();
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
