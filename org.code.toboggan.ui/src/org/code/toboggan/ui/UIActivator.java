package org.code.toboggan.ui;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.NetworkActivator;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.ui.preferences.SubscribedPreferencesController;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.WSManager;

public class UIActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.code.toboggan.ui";

	private static BundleContext context;
	private static UIActivator plugin;

	static BundleContext getContext() {
		return context;
	}

	public static SessionStorage getSessionStorage() {
		return CoreActivator.getSessionStorage();
	}

	public static WSManager getWSManager() {
		return NetworkActivator.getWSService().getWSManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		UIActivator.context = bundleContext;
		plugin = this;

		// Make sure all extensionManagers are initialized
		APIExtensionManager.getInstance();
		NetworkExtensionManager.getInstance();
		FileSystemExtensionManager.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		UIActivator.context = null;
		plugin = null;

		SubscribedPreferencesController.writeSubscribedProjects();
	}

	public static UIActivator getDefault() {
		return plugin;
	}
}
