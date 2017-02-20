package org.code.toboggan.ui;

import org.osgi.framework.BundleContext;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.WSManager;

public class UIActivator extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "org.code.toboggan.ui";

	private static BundleContext context;
	private static UIActivator plugin;

	static BundleContext getContext() {
		return context;
	}
	
	public SessionStorage getSessionStorage() {
		return null; // TODO: change to get session storage correctly
	}
	
	public WSManager getWSManager() {
		return null; // TODO: change to get ws manager correctly
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		UIActivator.context = bundleContext;
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		UIActivator.context = null;
		plugin = null;
	}
	
	public static UIActivator getDefault() {
		return plugin;
	}
}
