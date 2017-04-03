package org.code.toboggan.core;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

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
		resetExecutor();
	}

	public static void resetExecutor() {
		executor = new ThreadPoolExecutor(4, 20, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
	}

	public static ExecutorService getExecutor() {
		return executor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		CoreActivator.context = bundleContext;
		Path pluginStateLocation = getStateLocation().toFile().toPath();
		String logPath = pluginStateLocation.toString();
		System.setProperty("logPath", logPath);
		System.out.println("Setting log path to " + logPath);
		reset();
		
		// Log our current version
		Version version = FrameworkUtil.getBundle(getClass()).getVersion();		
		LogManager.getLogger(this.getClass()).info("Plugin version: " + version.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		CoreActivator.context = null;
		storage = null;
	}
}
