package org.code.toboggan.filesystem.extensions.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.WarnList;
import org.code.toboggan.filesystem.editor.DocumentManager;
import org.code.toboggan.filesystem.extensionpoints.FSExtensionIDs;
import org.code.toboggan.filesystem.extensionpoints.file.IFSFileMoveExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.notification.extensionpoints.file.IFileMoveNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.file.IFileMoveResponse;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
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
	public void fileMoved(long fileID, Path newFileLocation) {
		File file = ss.getFile(fileID);
		Project project = ss.getProject(file.getProjectID());

		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		Path projectLocation = p.getLocation().toFile().toPath();
		IFile newFile = p.getFile(projectLocation.relativize(newFileLocation).toString());

		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.FILE_MOVE_ID, IFSFileMoveExt.class);
		for (ICoreExtension ext : extensions) {
			IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
			moveExt.fileMoved(fileID, newFile, newFileLocation);
		}

		APIFactory.createFilePullDiffSendChanges(fileID).runAsync();
	}

	@Override
	public void fileMoveNotification(long fileID, Path newFileLocation) {
		File file = ss.getFile(fileID);
		Project project = ss.getProject(file.getProjectID());

		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		IFile oldFile = p.getFile(Paths.get(file.getRelativePath().toString(), file.getFilename()).toString());

		Path oldFileLocation = oldFile.getLocation().toFile().toPath();

		// Force close, to make sure changeListener is deregistered,
		// and re-registered upon the user opening the document
		// again
		// TODO: FIX THIS TO REMOVE THE NEED FOR CLOSING THE EDITOR
		ITextEditor editor = dm.getEditor(oldFileLocation);
		if (editor != null) {
			logger.debug("Closed editor for file " + oldFileLocation);
			editor.close(true);
		}

		moveFile(p, oldFile, fileID, project.getProjectID(), newFileLocation);

		// logger.debug(String.format("Updating documentManager path from [%s]
		// to
		// [%s]", oldFileLocation,
		// newFileLocation));
		// dm.pathChangedForEditor(fileLocation, newFileLocation);
	}

	@Override
	public void fileMoveFailed(long fileID, Path oldFileLocation, Path newFileLocation) {
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(new org.eclipse.core.runtime.Path(newFileLocation.toString()));
		NullProgressMonitor progressMonitor = new NullProgressMonitor();
		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.FILE_MOVE_ID, IFSFileMoveExt.class);

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
			APIFactory.createProjectUnsubscribe(file.getProjectID()).runAsync();
		}
	}

	private void moveFile(IProject project, IFile iFile, long fileID, long projectID, Path newFileLocation) {
		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.FILE_MOVE_ID, IFSFileMoveExt.class);

		if (iFile.exists()) {

			// new file (workspace-relative path)
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath newWorkspaceRelativePath = new org.eclipse.core.runtime.Path(
					workspace.getRoot().getLocation().toFile().toPath().relativize(newFileLocation).toString())
							.makeAbsolute();

			// Create folders if needed
			if (!newWorkspaceRelativePath.toString().equals("") && !newWorkspaceRelativePath.toString().equals(".")) {
				IPath newProjectRelativePath = new org.eclipse.core.runtime.Path(
						project.getLocation().toFile().toPath().relativize(newFileLocation).toString()).makeAbsolute();

				IPath currentFolder = new org.eclipse.core.runtime.Path("/");
				for (int i = 0; i < newProjectRelativePath.segmentCount() - 1; i++) {
					// iterate through path segments and create if they don't
					// exist
					currentFolder = (IPath) currentFolder.append(newProjectRelativePath.segment(i));
					logger.debug(String.format("Making folder [%s]", currentFolder.toString()));

					IFolder newFolder = project.getFolder(currentFolder);
					try {
						if (!newFolder.exists()) {
							newFolder.create(true, true, new NullProgressMonitor());
						}
					} catch (Exception e1) {
						logger.error(String.format("Could not create folder for [%s], unsubscribing",
								currentFolder.toString()), e1);
						for (ICoreExtension ext : extensions) {
							IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
							moveExt.folderCreationFailed(fileID, currentFolder.toString());
						}
						APIFactory.createProjectUnsubscribe(projectID).runAsync();
						return;
					}

				}

			}

			Path oldFileLocation = iFile.getLocation().toFile().toPath();
			try {
				NullProgressMonitor monitor = new NullProgressMonitor();
				warnList.putFileInWarnList(oldFileLocation, FileRenameNotification.class);
				// removing first segment to make it project relative
				iFile.move(newWorkspaceRelativePath, true, monitor);

				for (ICoreExtension ext : extensions) {
					IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
					moveExt.fileMoved(fileID, iFile, newFileLocation);
				}
				return;
			} catch (Exception e) {
				logger.error("Failed to move file for rename, unsubscribing", e);
				warnList.removeFileFromWarnList(oldFileLocation, FileRenameNotification.class);
				APIFactory.createProjectUnsubscribe(projectID).runAsync();
				for (ICoreExtension ext : extensions) {
					IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
					moveExt.moveFailed(fileID, iFile, newFileLocation);
				}
			}
		} else {
			logger.warn(
					String.format("Tried to rename file that does not exist: [%s]", iFile.getFullPath().toString()));
			for (ICoreExtension ext : extensions) {
				IFSFileMoveExt moveExt = (IFSFileMoveExt) ext;
				moveExt.moveFileNotFound(fileID);
			}
		}
	}

}
