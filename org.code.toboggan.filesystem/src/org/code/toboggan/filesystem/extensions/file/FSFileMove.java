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
import org.code.toboggan.filesystem.extensionpoints.file.IFSFileMoveExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.request.extensionpoints.file.IFileMoveResponse;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import clientcore.websocket.models.File;

public class FSFileMove implements IFileMoveResponse {
	private Logger logger = LogManager.getLogger(FSFileMove.class);

	private AbstractExtensionManager extMgr;
	
	public FSFileMove() {
		this.extMgr = FileSystemExtensionManager.getInstance();
	}
	
	@Override
	public void fileMoved(long fileID, Path newFileLocation) {
		new Thread(APIFactory.createFilePullDiffSendChanges(fileID)).start();
	}

	@Override
	public void fileMoveFailed(long fileID, Path oldFileLocation, Path newFileLocation) {
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(new org.eclipse.core.runtime.Path(newFileLocation.toString()));
		NullProgressMonitor progressMonitor = new NullProgressMonitor();
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_MOVE_ID);
		
		try {
			iFile.move(new org.eclipse.core.runtime.Path(oldFileLocation.toString()), true, progressMonitor);
			
			for (ICoreExtension ext : extensions) {
				IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
				moveExt.moveUndone(fileID, iFile, oldFileLocation, newFileLocation);
			}
			
		} catch (CoreException e) {
			logger.error("Failed to undo move, unsubscribing from project", e);
			for (ICoreExtension ext : extensions) {
				IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
				moveExt.undoFailed(fileID, iFile, oldFileLocation, newFileLocation);
			}
			
			File file = CoreActivator.getSessionStorage().getFile(fileID);
			new Thread(APIFactory.createProjectUnsubscribe(file.getProjectID())).start();
		}
	}

}
