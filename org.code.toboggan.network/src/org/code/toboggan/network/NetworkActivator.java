package org.code.toboggan.network;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class NetworkActivator implements BundleActivator {

	public static final String PLUGIN_ID = "org.code.toboggan.network";
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		NetworkActivator.context = bundleContext;
		// Start WS connection when this plugin is activated
		WSService.getWSManager().connect();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		NetworkActivator.context = null;
		WSService.getWSManager().close();
	}

}
