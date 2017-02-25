package org.code.toboggan.filesystem.extensions.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.filesystem.CCIgnore;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.WarnList;
import org.code.toboggan.filesystem.editor.DocumentManager;
import org.code.toboggan.filesystem.extensionpoints.file.IFSFileDeleteExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.notification.extensionpoints.file.IFileDeleteNotificationExtension;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.notifications.FileDeleteNotification;

public class FSFileDelete implements IFileDeleteNotificationExtension {
	private Logger logger = LogManager.getLogger(FSFileDelete.class);
	
	private SessionStorage ss;
	private DocumentManager dm;
	private WarnList warnList;
	private AbstractExtensionManager extMgr;
	
	public FSFileDelete() {
		this.ss = CoreActivator.getSessionStorage();
		this.dm = FSActivator.getDocumentManager();
		this.warnList = FSActivator.getWarnList();
		this.extMgr = FileSystemExtensionManager.getInstance();
	}
	
	@Override
	public void fileDeleteNotification(long deletedId) {
		File file = ss.getFile(deletedId);
		if (file == null) {
			logger.warn("Received delete notification for a file that does not exist in storage");
			return;
		}
		
		Project project = ss.getProject(file.getProjectID());
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		IFile iFile = p.getFile(Paths.get(file.getRelativePath().toString(), file.getFilename()).toString());
		String workspaceRelativePath = iFile.getFullPath().toString();
		
		CCIgnore ignoreFile = CCIgnore.createForProject(p);
		if (ignoreFile.containsEntry(file.getRelativePath().toString())) {
			logger.info(String.format("did not delete %s because it was excluded by .ccignore", file.getRelativePath().toString()));
			return;
		}
		Path fileLocation = iFile.getLocation().toFile().toPath();

		
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_DELETE_ID);

		if (iFile.exists()) {
			if (dm.getEditor(fileLocation) != null) {
				// TODO: implement new API call for TryRestore (not part of client refactor)
//				Display.getDefault().asyncExec(() -> {
//					String message = String.format(DialogStrings.DeleteWarningDialog_Message, file.getName());
//					OkCancelDialog dialog = OkCancelDialog.createDialog(message,
//							"Restore", IDialogConstants.OK_LABEL, true);
//					// Restore is the "ok" option, because it should not be the default option
//					if (dialog.open() == Window.OK) {
//						tryRestoreFile(resId, file, meta, project.getProjectID());
//					} else {
//						deleteFile(workspaceRelativePath, file, resId, project);
//					}
//				});
//				return;
				for (ICoreExtension e : extensions) {
					IFSFileDeleteExt ext = (IFSFileDeleteExt) e;
					ext.fileOpenInEditor(deletedId, fileLocation);
				}
			}
			try {
				warnList.putFileInWarnList(fileLocation, FileDeleteNotification.class);
				iFile.delete(true, new NullProgressMonitor());
				
				for (ICoreExtension ext : extensions) {
					IFSFileDeleteExt delExt = (IFSFileDeleteExt) ext;
					delExt.fileDeleted(deletedId);
				}
				return;
			} catch (CoreException e) {
				logger.error("Failed to delete file on disk, unsubscribing from project", e);
				warnList.removeFileFromWarnList(fileLocation, FileDeleteNotification.class);
				for (ICoreExtension ext : extensions) {
					IFSFileDeleteExt delExt = (IFSFileDeleteExt) ext;
					delExt.deleteFailed(deletedId, fileLocation);
				}
				return;
			}
		} else {
			logger.warn(String.format("Tried to delete file that does not exist: %s", workspaceRelativePath));
		}
	}

}
