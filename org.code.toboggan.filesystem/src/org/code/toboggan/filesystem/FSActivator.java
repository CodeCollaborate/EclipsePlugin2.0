package org.code.toboggan.filesystem;

import org.code.toboggan.filesystem.editor.DocumentManager;
import org.code.toboggan.filesystem.editor.listeners.DirectoryListener;
import org.code.toboggan.filesystem.editor.listeners.EditorChangeListener;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class FSActivator implements BundleActivator {

	private static BundleContext context;
	
	private static DocumentManager documentManager;
	private static WarnList warnList;
	
	// listeners
	private EditorChangeListener editorChangeListener;
	private DirectoryListener dirListener;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		FSActivator.context = bundleContext;
		
		// instantiate persistent objects
		documentManager = new DocumentManager();
		warnList = new WarnList();
		
		registerEditorListener();
		registerResourceListeners();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		FSActivator.context = null;
		
		deregisterResourceListeners();
	}
	
	public static DocumentManager getDocumentManager() {
		return documentManager;
	}
	
	public static WarnList getWarnList() {
		return warnList;
	}
	
	private void registerResourceListeners() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		dirListener = new DirectoryListener();
		workspace.addResourceChangeListener(dirListener, IResourceChangeEvent.POST_BUILD);
	}
	
	private void deregisterResourceListeners() {
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
