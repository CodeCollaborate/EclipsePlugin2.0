package org.code.toboggan.filesystem.extensions.file;

import java.nio.file.Path;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.filesystem.extensionpoints.file.IFSFileRenameExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.request.extensionpoints.file.IFileRenameResponse;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import clientcore.websocket.models.File;

public class FSFileRename implements IFileRenameResponse {
	private Logger logger = LogManager.getLogger(FSFileRename.class);

	private AbstractExtensionManager extMgr;
	
	public FSFileRename() {
		this.extMgr = FileSystemExtensionManager.getInstance();
	}
	
	@Override
	public void fileRenamed(long fileID, Path newFileLocation, String newName) {
		new Thread(APIFactory.createFilePullDiffSendChanges(fileID)).start();
	}

	@Override
	public void fileRenameFailed(long fileID, Path oldFileLocation, Path newFileLocation, String newName) {
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(new org.eclipse.core.runtime.Path(newFileLocation.toString()));
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_MOVE_ID);
		File file = CoreActivator.getSessionStorage().getFile(fileID);
	
		logger.error("Failed rename, unsubscribing from project");
		for (ICoreExtension ext : extensions) {
			IFSFileRenameExt moveExt = (IFSFileRenameExt) ext;
			moveExt.undoFailed(fileID, newName, iFile, oldFileLocation, newFileLocation);
		}
		
		new Thread(APIFactory.createProjectUnsubscribe(file.getProjectID())).start();
	}

}
