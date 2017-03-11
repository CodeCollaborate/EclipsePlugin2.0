package org.code.toboggan.filesystem.extensions.project;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.WarnList;
import org.code.toboggan.filesystem.extensionpoints.FSExtensionIDs;
import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectSubscribeExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.request.extensionpoints.project.IProjectSubscribeResponse;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import clientcore.dataMgmt.ProjectController;
import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.notifications.ProjectDeleteNotification;
import clientcore.websocket.models.requests.ProjectCreateRequest;

public class FSProjectSubscribe implements IProjectSubscribeResponse {

private static Logger logger = LogManager.getLogger(FSProjectSubscribe.class);
	
	private SessionStorage ss;
	private ProjectController pc;
	private WarnList warnList;
	private AbstractExtensionManager extMgr;
	
	public FSProjectSubscribe() {
		this.ss = CoreActivator.getSessionStorage();
		this.pc = new ProjectController(ss);
		this.warnList = FSActivator.getWarnList();
		this.extMgr = FileSystemExtensionManager.getInstance();
	}
	
	@Override
	public void subscribed(long projectID, List<File> files) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Project p = ss.getProject(projectID);
		IProject iProject = root.getProject(p.getName());
		NullProgressMonitor progressMonitor = new NullProgressMonitor();
		
		// create & open a new project, deleting the old one if it exists
		try {
			if (iProject.exists()) {
				// using false for the "deleteContent" flag so that this doesn't aggressively delete files out
				// from underneath the user upon subscribing (and file deletes were being propagated to the server)
				warnList.putProjectInWarnList(p.getName(), ProjectDeleteNotification.class);
				iProject.delete(false, true, progressMonitor);
			}
			warnList.putProjectInWarnList(p.getName(), ProjectCreateRequest.class);
			iProject.create(progressMonitor);
			iProject.open(progressMonitor);
		} catch (CoreException e) {
			logger.error("Failed to create project", e);
		}
						
		files.forEach((f) -> APIFactory.createFilePull(f.getFileID()).runAsync());
		
		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.PROJECT_SUBSCRIBE_ID, IFSProjectSubscribeExt.class);
		for (ICoreExtension e : extensions) {
			IFSProjectSubscribeExt createExt = (IFSProjectSubscribeExt) e;
			createExt.subscribed(p, iProject, files.toArray(new File[]{}));
		}
	}

	@Override
	public void subscribeFailed(long projectID) {
		// TODO error handling
	}

}
