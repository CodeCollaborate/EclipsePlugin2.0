package org.code.toboggan.filesystem;

import org.code.toboggan.filesystem.editor.DocumentManager;
import org.code.toboggan.filesystem.editor.ShadowDocumentManager;
import org.code.toboggan.filesystem.editor.listeners.DirectoryListener;
import org.code.toboggan.filesystem.editor.listeners.EditorChangeListener;
import org.code.toboggan.network.NetworkActivator;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class FSActivator implements BundleActivator {

	public static final String PLUGIN_ID = "org.code.toboggan.filesystem";
	private static BundleContext context;

	private static ShadowDocumentManager shadowDocumentManager;
	private static DocumentManager documentManager;
	private static WarnList warnList;

	// listeners
	private static EditorChangeListener editorChangeListener;
	private static DirectoryListener dirListener;

	static BundleContext getContext() {
		return context;
	}

	public static EditorChangeListener getEditorChangeListener() {
		return editorChangeListener;
	}

	public static DocumentManager getDocumentManager() {
		return documentManager;
	}

	public static ShadowDocumentManager getShadowDocumentManager() {
		return shadowDocumentManager;
	}

	public static WarnList getWarnList() {
		if (warnList == null) {
			synchronized (FSActivator.class) {
				if (warnList == null) {
					warnList = new WarnList();
				}
			}
		}
		return warnList;
	}

	public static void reset() {
		warnList = new WarnList();
		documentManager = new DocumentManager();
		shadowDocumentManager = new ShadowDocumentManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		FSActivator.context = bundleContext;

		// instantiate persistent objects
		reset();

		registerEditorListener();
		registerResourceListeners();

		NetworkActivator.getPatchManager().setNotifHandler(documentManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		FSActivator.context = null;

		deregisterResourceListeners();
	}

	public static void registerResourceListeners() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		dirListener = new DirectoryListener();
		workspace.addResourceChangeListener(dirListener, IResourceChangeEvent.POST_CHANGE);
	}

	public static void deregisterResourceListeners() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(dirListener);
	}

	private void registerEditorListener() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				editorChangeListener = new EditorChangeListener();
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService()
						.addPartListener(editorChangeListener);
			}
		});
	}
}
