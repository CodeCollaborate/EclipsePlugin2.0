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
import org.code.toboggan.filesystem.extensionpoints.file.IFSFileRenameExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.notification.extensionpoints.file.IFileRenameNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.file.IFileRenameResponse;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.texteditor.ITextEditor;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.notifications.FileRenameNotification;

public class FSFileRename implements IFileRenameResponse, IFileRenameNotificationExtension {
	private Logger logger = LogManager.getLogger(FSFileRename.class);

	private AbstractExtensionManager extMgr;
	private SessionStorage ss;
	private DocumentManager dm;
	private WarnList warnList;

	public FSFileRename() {
		this.extMgr = FileSystemExtensionManager.getInstance();
		this.ss = CoreActivator.getSessionStorage();
		this.dm = FSActivator.getDocumentManager();
		this.warnList = FSActivator.getWarnList();
	}

	@Override
	public void fileRenamed(long fileID, Path newFileLocation, String newName) {
		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.FILE_RENAME_ID, IFSFileRenameExt.class);
		for (ICoreExtension ext : extensions) {
			IFSFileRenameExt renameExt = (IFSFileRenameExt) ext;
			renameExt.fileRenamed(fileID, newName, newFileLocation);
		}

		APIFactory.createFilePullDiffSendChanges(fileID).runAsync();
	}

	@Override
	public void fileRenameNotification(long fileID, Path newPath, String newName) {
		File file = ss.getFile(fileID);
		if (file == null) {
			logger.warn("Received File.Rename notification for non-existent file.");
			return;
		}

		// old file
		Path oldFileLocation = ss.getProjectLocation(file.getProjectID()).resolve(file.getRelativePath())
				.resolve(file.getFilename());

		// Force close, to make sure changeListener is deregistered,
		// and re-registered upon the user opening the document
		// again
		// TODO: FIX THIS TO REMOVE THE NEED FOR CLOSING THE EDITOR
		ITextEditor editor = dm.getEditor(oldFileLocation);
		if (editor != null) {
			logger.debug("Closed editor for file " + oldFileLocation);
			editor.close(true);
		}

		moveFile(file, newPath, newName);

		// logger.debug(String.format("Updating documentManager path from [%s]
		// to
		// [%s]", oldFileLocation,
		// newPath));
		// dm.pathChangedForEditor(oldFileLocation, newPath);
	}

	@Override
	public void fileRenameFailed(long fileID, Path oldFileLocation, Path newFileLocation, String newName) {
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(new org.eclipse.core.runtime.Path(newFileLocation.toString()));
		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.FILE_RENAME_ID, IFSFileRenameExt.class);
		File file = CoreActivator.getSessionStorage().getFile(fileID);

		logger.error("Failed rename, unsubscribing from project");
		for (ICoreExtension ext : extensions) {
			IFSFileRenameExt moveExt = (IFSFileRenameExt) ext;
			moveExt.renameUndoFailed(fileID, newName, iFile, oldFileLocation, newFileLocation);
		}

		APIFactory.createProjectUnsubscribe(file.getProjectID()).runAsync();
	}

	private void moveFile(File file, Path newPath, String newName) {
		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.FILE_RENAME_ID, IFSFileRenameExt.class);

		Project project = ss.getProject(file.getProjectID());
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		IFile iFile = p.getFile(Paths.get(file.getRelativePath().toString(), file.getFilename()).toString());

		// new file (workspace-relative path)
		IPath newWorkspaceRelativePath = new org.eclipse.core.runtime.Path(project.getName())
				.append(file.getRelativePath().toString()).append(newName).makeAbsolute();

		if (iFile.exists()) {

			// Create folders if needed
			if (!newWorkspaceRelativePath.toString().equals("") && !newWorkspaceRelativePath.toString().equals(".")) {
				IPath projectRelativePath = newWorkspaceRelativePath.removeFirstSegments(1);

				IPath currentFolder = new org.eclipse.core.runtime.Path("/");
				for (int i = 0; i < projectRelativePath.segmentCount() - 1; i++) {
					// iterate through path segments and create if they don't
					// exist
					currentFolder = (IPath) currentFolder.append(projectRelativePath.segment(i));
					logger.debug(String.format("Making folder [%s]", currentFolder.toString()));

					IFolder newFolder = p.getFolder(currentFolder);
					try {
						if (!newFolder.exists()) {
							newFolder.create(true, true, new NullProgressMonitor());
						}
					} catch (Exception e1) {
						logger.error(String.format("Could not create folder for [%s], unsubscribing",
								currentFolder.toString()), e1);
						for (ICoreExtension ext : extensions) {
							IFSFileRenameExt moveExt = (IFSFileRenameExt) ext;
							moveExt.folderCreationFailed(file.getFileID(), currentFolder.toString());
						}
						APIFactory.createProjectUnsubscribe(file.getProjectID()).runAsync();
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
					IFSFileRenameExt moveExt = (IFSFileRenameExt) ext;
					moveExt.fileRenamed(file.getFileID(), newName, newPath);
				}
				return;
			} catch (Exception e) {
				logger.error("Failed to move file for rename, unsubscribing", e);
				warnList.removeFileFromWarnList(oldFileLocation, FileRenameNotification.class);
				APIFactory.createProjectUnsubscribe(file.getProjectID()).runAsync();
				for (ICoreExtension ext : extensions) {
					IFSFileRenameExt moveExt = (IFSFileRenameExt) ext;
					moveExt.renameFailed(file.getFileID(), newName);
				}
			}
		} else {
			logger.warn(
					String.format("Tried to rename file that does not exist: [%s]", iFile.getFullPath().toString()));
			for (ICoreExtension ext : extensions) {
				IFSFileRenameExt moveExt = (IFSFileRenameExt) ext;
				moveExt.renameFileNotFound(file.getFileID(), newName);
			}
		}
	}

}
