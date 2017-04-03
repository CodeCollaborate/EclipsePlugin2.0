package org.code.toboggan.filesystem.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.filesystem.extensionpoints.FSExtensionIDs;
import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectRenameExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectRenameNotificationExtension;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import clientcore.websocket.models.Project;

public class FSProjectRename implements IProjectRenameNotificationExtension {
	private Logger logger = LogManager.getLogger(this.getClass());

	private AbstractExtensionManager extMgr;

	public FSProjectRename() {
		this.extMgr = FileSystemExtensionManager.getInstance();
	}

	@Override
	public void projectRenameNotification(long projectID, String newName) {
		Project project = CoreActivator.getSessionStorage().getProject(projectID);
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		IPath newPath = iProject.getFullPath().removeLastSegments(1).append(newName);

		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.PROJECT_RENAME_ID,
				IFSProjectRenameExt.class);
		try {
			iProject.move(newPath, true, new NullProgressMonitor());

			for (ICoreExtension ext : extensions) {
				IFSProjectRenameExt renameExt = (IFSProjectRenameExt) ext;
				renameExt.projectRenamed(projectID, newName);
			}
		} catch (CoreException e) {
			logger.error("Failed to rename project", e);

			for (ICoreExtension ext : extensions) {
				IFSProjectRenameExt renameExt = (IFSProjectRenameExt) ext;
				renameExt.projectRenamedFailed(projectID);
			}
		}
	}
}
