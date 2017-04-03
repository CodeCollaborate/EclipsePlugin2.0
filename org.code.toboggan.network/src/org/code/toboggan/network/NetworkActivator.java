package org.code.toboggan.network;

import org.code.toboggan.network.notification.clientcorelisteners.file.FileCreateNotificationHandler;
import org.code.toboggan.network.notification.clientcorelisteners.file.FileDeleteNotificationHandler;
import org.code.toboggan.network.notification.clientcorelisteners.file.FileMoveNotificationHandler;
import org.code.toboggan.network.notification.clientcorelisteners.file.FileRenameNotificationHandler;
import org.code.toboggan.network.notification.clientcorelisteners.project.ProjectDeleteNotificationHandler;
import org.code.toboggan.network.notification.clientcorelisteners.project.ProjectGrantPermissionsNotificationHandler;
import org.code.toboggan.network.notification.clientcorelisteners.project.ProjectRenameNotificationHandler;
import org.code.toboggan.network.notification.clientcorelisteners.project.ProjectRevokePermissionsNotificationHandler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import clientcore.patching.PatchManager;
import clientcore.websocket.WSManager;

public class NetworkActivator implements BundleActivator {

	public static final String PLUGIN_ID = "org.code.toboggan.network";
	private static BundleContext context;
	private static PatchManager patchManager;
	private static WSService wsService;

	static BundleContext getContext() {
		return context;
	}

	public static PatchManager getPatchManager() {
		return patchManager;
	}

	public static void reset() {
		patchManager = new PatchManager();

		patchManager.setWsMgr(getWSService().getWSManager());
		registerNotificationHandlers();
	}

	public static WSService getWSService() {
		if (wsService == null) {
			synchronized (NetworkActivator.class) {
				if (wsService == null) {
					wsService = new WSService();
				}
			}
		}
		return wsService;
	}

	public static void setWSService(WSService wsService) {
		NetworkActivator.wsService = wsService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		NetworkActivator.context = bundleContext;

		reset();

	}

	public static void registerNotificationHandlers() {
		WSManager wsMgr = getWSService().getWSManager();

		wsMgr.registerNotificationHandler("Project", "Delete", new ProjectDeleteNotificationHandler());
		wsMgr.registerNotificationHandler("Project", "GrantPermissions",
				new ProjectGrantPermissionsNotificationHandler());
		wsMgr.registerNotificationHandler("Project", "Rename", new ProjectRenameNotificationHandler());
		wsMgr.registerNotificationHandler("Project", "RevokePermissions",
				new ProjectRevokePermissionsNotificationHandler());
		wsMgr.registerNotificationHandler("File", "Change", patchManager);
		wsMgr.registerNotificationHandler("File", "Create", new FileCreateNotificationHandler());
		wsMgr.registerNotificationHandler("File", "Delete", new FileDeleteNotificationHandler());
		wsMgr.registerNotificationHandler("File", "Move", new FileMoveNotificationHandler());
		wsMgr.registerNotificationHandler("File", "Rename", new FileRenameNotificationHandler());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		NetworkActivator.context = null;
		patchManager = null;
		wsService.getWSManager().close();
	}

}
