package org.code.toboggan.filesystem.extensions.project;

import java.util.Set;

import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectRenameExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.notification.extensionpoints.project.IProjectRenameNotificationExtension;

public class FSProjectRename implements IProjectRenameNotificationExtension {

	private AbstractExtensionManager extMgr;
	
	public FSProjectRename() {
		this.extMgr = FileSystemExtensionManager.getInstance();
	}
	
	@Override
	public void projectRenamed(long projectID, String newName) {
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_RENAME_ID);
		for (ICoreExtension ext : extensions) {
			IFSProjectRenameExt renameExt = (IFSProjectRenameExt) ext;
			renameExt.projectRenamed(projectID, newName);
		}
	}

}
