package org.code.toboggan.filesystem.extensions.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.WarnList;
import org.code.toboggan.filesystem.editor.DocumentManager;
import org.code.toboggan.filesystem.extensionpoints.file.IFSFileMoveExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.notification.extensionpoints.file.IFileMoveNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.file.IFileMoveResponse;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.texteditor.ITextEditor;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.notifications.FileRenameNotification;

public class FSFileMove implements IFileMoveResponse, IFileMoveNotificationExtension {
	private Logger logger = LogManager.getLogger(FSFileMove.class);

	private AbstractExtensionManager extMgr;
	private SessionStorage ss;
	private WarnList warnList;
	private DocumentManager dm;
	
	public FSFileMove() {
		this.extMgr = FileSystemExtensionManager.getInstance();
		this.ss = CoreActivator.getSessionStorage();
		this.warnList = FSActivator.getWarnList();
		this.dm = FSActivator.getDocumentManager();
	}
	
	@Override
	public void fileMoved(long fileID, Path newWorkspaceRelativePath) {
		new Thread(APIFactory.createFilePullDiffSendChanges(fileID)).start();
	}
	
	@Override
	public void fileMoveNotification(long fileID, Path newFileLocation) {
		File file = ss.getFile(fileID);
		Project project = ss.getProject(file.getProjectID());
		
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		Path projectLocation = p.getLocation().toFile().toPath();
		IFile oldFile = p.getFile(Paths.get(file.getRelativePath().toString(), file.getFilename()).toString());
		
		// new file (workspace-relative path)
		IPath newPathToFile = new org.eclipse.core.runtime.Path(project.getName()).append(
				projectLocation.relativize(newFileLocation).toString()).makeAbsolute();
		
		// Force close, to make sure changelistener doesn't fire.
		Path fileLocation = oldFile.getLocation().toFile().toPath();
		ITextEditor editor = dm.getEditor(fileLocation);
		if(editor != null){
			logger.error("Closed editor for file " + fileLocation.toString());
			dm.closedDocument(fileLocation);
			editor.close(false);								
		}
		
		moveFile(p, oldFile, newPathToFile, fileID, project.getProjectID(), newFileLocation);
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
				moveExt.moveUndoFailed(fileID, iFile, oldFileLocation, newFileLocation);
			}
			
			File file = CoreActivator.getSessionStorage().getFile(fileID);
			new Thread(APIFactory.createProjectUnsubscribe(file.getProjectID())).start();
		}
	}
	
	private void moveFile(IProject p, IFile iFile, IPath newWorkspaceRelativePath, long fileID, long projectID, Path newFileLocation) {
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_MOVE_ID);
		
		if (iFile.exists()) {
			
			// Create folders if needed
			if (!newWorkspaceRelativePath.toString().equals("") && !newWorkspaceRelativePath.toString().equals(".")) {
				IPath projectRelativePath = newWorkspaceRelativePath.removeFirstSegments(1);
				
				IPath currentFolder = new org.eclipse.core.runtime.Path("/");
				for (int i = 0; i < projectRelativePath.segmentCount() - 1; i++) {
					// iterate through path segments and create if they don't exist
					currentFolder = (IPath) currentFolder.append(projectRelativePath.segment(i));
					logger.debug(String.format("Making folder %s", currentFolder.toString()));
					
					IFolder newFolder = p.getFolder(currentFolder);
					try {
						if (!newFolder.exists()) {
							newFolder.create(true, true, new NullProgressMonitor());
						}
					} catch (Exception e1) {
						logger.error(String.format("Could not create folder for %s, unsubscribing", currentFolder.toString()), e1);
						for (ICoreExtension ext : extensions) {
							IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
							moveExt.folderCreationFailed(fileID, currentFolder.toString());
						}
						new Thread(APIFactory.createProjectUnsubscribe(projectID)).start();
						return;
					}
					
				}
				
			}
			
			Path fileLocation = iFile.getLocation().toFile().toPath();
			try {
				NullProgressMonitor monitor = new NullProgressMonitor();
				warnList.putFileInWarnList(fileLocation, FileRenameNotification.class);
				// removing first segment to make it project relative
				iFile.move(newWorkspaceRelativePath, true, monitor);
				
				for (ICoreExtension ext : extensions) {
					IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
					moveExt.fileMoved(fileID, iFile, newFileLocation);
				}
				return;
			} catch (Exception e) {
				logger.error("Failed to move file for rename, unsubscribing", e);
				warnList.removeFileFromWarnList(fileLocation, FileRenameNotification.class);
				new Thread(APIFactory.createProjectUnsubscribe(projectID)).start();
				for (ICoreExtension ext : extensions) {
					IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
					moveExt.moveFailed(fileID, iFile, newFileLocation);
				}
			}
		} else {
			logger.warn(String.format("Tried to rename file that does not exist: %s", iFile.getFullPath().toString()));
			for (ICoreExtension ext : extensions) {
				IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
				moveExt.moveFileNotFound(fileID);
			}
		}
	}

}
