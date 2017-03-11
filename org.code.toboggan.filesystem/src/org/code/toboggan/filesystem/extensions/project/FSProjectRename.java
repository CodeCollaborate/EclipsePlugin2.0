package org.code.toboggan.filesystem.extensions.project;

import java.util.Set;

import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.filesystem.extensionpoints.FSExtensionIDs;
import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectRenameExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectRenameNotificationExtension;

public class FSProjectRename implements IProjectRenameNotificationExtension {

	private AbstractExtensionManager extMgr;
	
	public FSProjectRename() {
		this.extMgr = FileSystemExtensionManager.getInstance();
	}
	
	@Override
	public void projectRenameNotification(long projectID, String newName) {
		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.PROJECT_RENAME_ID, IFSProjectRenameExt.class);
		for (ICoreExtension ext : extensions) {
			IFSProjectRenameExt renameExt = (IFSProjectRenameExt) ext;
			renameExt.projectRenamed(projectID, newName);
		}
	}
}
