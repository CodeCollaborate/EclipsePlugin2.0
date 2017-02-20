package org.code.toboggan.network;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import clientcore.patching.PatchManager;
import clientcore.websocket.WSManager;

public class NetworkActivator implements BundleActivator {

	public static final String PLUGIN_ID = "org.code.toboggan.network";
	private static BundleContext context;
	private static PatchManager patchManager;
	
	static BundleContext getContext() {
		return context;
	}
	
	public static PatchManager getPatchManager() {
		return patchManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		NetworkActivator.context = bundleContext;
		// Start WS connection when this plugin is activated
		WSManager m = WSService.getWSManager();
		patchManager = new PatchManager();
		patchManager.setWsMgr(m);
		m.connect();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		NetworkActivator.context = null;
		patchManager = null;
		WSService.getWSManager().close();
	}

}
