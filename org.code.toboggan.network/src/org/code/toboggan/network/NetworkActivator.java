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
	}
	
	public static WSService getWSService() {
		if (wsService == null){
			synchronized(NetworkActivator.class){
				if(wsService == null){
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
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		NetworkActivator.context = bundleContext;
		// Start WS connection when this plugin is activated
		getWSService();
		
		reset();
		patchManager.setWsMgr(wsService.getWSManager());
		registerNotificationHandlers(wsService.getWSManager());

		// Don't connect - let the websocket lazily connect upon first request
//		 wsService.getWSManager().connect();
	}
	
	private void registerNotificationHandlers(WSManager m) {
		m.registerNotificationHandler("Project", "Delete", new ProjectDeleteNotificationHandler());
		m.registerNotificationHandler("Project", "GrantPermissions", new ProjectGrantPermissionsNotificationHandler());
		m.registerNotificationHandler("Project", "Rename", new ProjectRenameNotificationHandler());
		m.registerNotificationHandler("Project", "RevokePermissions", new ProjectRevokePermissionsNotificationHandler());
		m.registerNotificationHandler("File", "Create", new FileCreateNotificationHandler());
		m.registerNotificationHandler("File", "Delete", new FileDeleteNotificationHandler());
		m.registerNotificationHandler("File", "Move", new FileMoveNotificationHandler());
		m.registerNotificationHandler("File", "Rename", new FileRenameNotificationHandler());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		NetworkActivator.context = null;
		patchManager = null;
		wsService.getWSManager().close();
	}

}
