package org.code.toboggan.filesystem.editor.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.filesystem.CCIgnore;
import org.code.toboggan.filesystem.util.FSUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.texteditor.ITextEditor;

import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.notifications.FileCreateNotification;
import clientcore.websocket.models.notifications.FileDeleteNotification;
import clientcore.websocket.models.notifications.FileMoveNotification;
import clientcore.websocket.models.notifications.FileRenameNotification;
import clientcore.websocket.models.notifications.ProjectDeleteNotification;
import clientcore.websocket.models.notifications.ProjectRenameNotification;
import clientcore.websocket.models.requests.FileChangeRequest;
import clientcore.websocket.models.responses.FileCreateResponse;
import clientcore.websocket.models.responses.ProjectCreateResponse;

public class DirectoryListener extends AbstractDirectoryListener {
	private final Logger logger = LogManager.getLogger("directoryListener");

	/**
	 * Handles a resource delta in the case that the resource is and IProject.
	 * 
	 * @param delta
	 * @return true if the recursion down the delta's children stops after this
	 *         handler
	 */
	@Override
	protected boolean handleProject(IResourceDelta delta) {
		IProject iProject = (IProject) delta.getResource();
		Project project = ss.getProject(iProject.getLocation().toFile().toPath());

		if (delta.getKind() == IResourceDelta.REMOVED) {
			// Project was renamed
			if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
				if (warnList.isProjectInWarnList(iProject.getName(), ProjectRenameNotification.class)) {
					warnList.removeProjectFromWarnList(iProject.getName(), ProjectRenameNotification.class);
				} else {
					String newName = delta.getMovedToPath().lastSegment();
					String newPath = delta.getMovedToPath().toString();

					new Thread(APIFactory.createProjectRename(project.getProjectID(), newName)).start();
					;
					logger.debug(
							String.format("sent project rename request: renamed to \"%s\"; path changed to : \"%s\"",
									newName, newPath));
					return false;
				}
			} else {
				if (warnList.isProjectInWarnList(iProject.getName(), ProjectDeleteNotification.class)) {
					warnList.removeProjectFromWarnList(iProject.getName(), ProjectDeleteNotification.class);
					return true;
				} else {
					logger.debug("Deleting project");
					// Project was deleted from disk
					logger.debug("Unsubscribed from project due to removal from disk");

					APIFactory.createProjectUnsubscribe(project.getProjectID());
					return true;
				}
			}
		} else if (delta.getKind() == IResourceDelta.ADDED) {
			if (warnList.isProjectInWarnList(iProject.getName(), ProjectCreateResponse.class)) {
				warnList.removeProjectFromWarnList(iProject.getName(), ProjectCreateResponse.class);
			}

			return true;
		}

		return false;
	}

	@Override
	protected void handleFile(IResourceDelta delta) {
		IFile iFile = (IFile) delta.getResource();
		Path fileLocation = iFile.getLocation().toFile().toPath();
		File file = ss.getFile(fileLocation);

		logger.debug(String.format("Filename: %s File flag: %d", iFile.getName(), delta.getFlags()));

		if (delta.getKind() == IResourceDelta.CHANGED) {
			if (file == null) {
				// rather than deleting the document if metadata doesn't exist,
				// we want to
				// a) check it's not in the .ccignore
				// b) if it's not, add it to the server
				logger.info("No metadata found for file change event, resolving");
				createFile(iFile);
			}

			if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {

				IPath relativeMovedToPath = delta.getMovedToPath().removeFirstSegments(1);
				if (!relativeMovedToPath.toString().equals(iFile.getProjectRelativePath().toString())) {

					// Force close, to make sure changelistener doesn't fire.
					Path absMovedFromPath = FSUtils
							.getLocationForRelativePath(delta.getMovedFromPath().toFile().toPath());
					ITextEditor editor = documentManager.getEditor(absMovedFromPath);
					if (editor != null) {
						System.out.println("Closed editor for file " + absMovedFromPath);
						editor.close(false);
					}

					if (iFile.getName().equals(relativeMovedToPath.lastSegment())) {
						// send File.Move request
						changedFileMove(delta, fileLocation);
					} else {
						// send File.Rename request
						String newName = relativeMovedToPath.lastSegment();
						changedFileRename(absMovedFromPath, fileLocation, file, newName);
					}

				}

			} else if ((delta.getFlags() & IResourceDelta.CONTENT) != 0) {
				// don't diff this if this is the actively open file
				Path currFile = documentManager.getCurrFile();
				if (currFile != null) {
					if (currFile.equals(fileLocation)) {
						logger.debug("Save did not trigger diffing for active document.");
						return;
					}
				}

				if ((delta.getFlags() & IResourceDelta.REPLACED) != 0) {
					logger.debug(String.format("File contents were replaced for %s", fileLocation));
					return;
				}

				if (warnList.isFileInWarnList(fileLocation, FileChangeRequest.class)) {
					warnList.removeFileFromWarnList(fileLocation, FileChangeRequest.class);
				} else {
					if (file == null) {
						// file should have metadata but doesn't, so send a
						// request to make the file
						createFile(iFile);
					} else {
						// I'm really at a loss as to how to make sure this
						// isn't triggering directly after the file
						// is pulled w/out causing side effects. I thought about
						// putting in at EclipseRequestManager:100,
						// but I'm not sure it's a good idea because it may
						// never get removed.
						//
						// The problem stems from the fact that this case of
						// IResource flags isn't specific enough to
						// differentiate when the file is being written to after
						// closing the editor vs from the plugin
						// itself.
						//
						// I tried looking at IResourceDelta codes and trying to
						// find the more specific case where a file
						// is replaced, but we're already checking
						// IResourceDelta.REPLACED, so I'm quite sure why that's
						// not being flipped.
						//
						// If you have an idea, please let me know - Joel
						// (jshap70)
						new Thread(APIFactory.createFilePullDiffSendChanges(file.getFileID())).start();
					}
				}
			}

		} else if (delta.getKind() == IResourceDelta.REMOVED) {
			if ((delta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
				// File was deleted from disk
				if (warnList.isFileInWarnList(fileLocation, FileDeleteNotification.class)) {
					warnList.removeFileFromWarnList(fileLocation, FileDeleteNotification.class);
				} else {
					if (file == null) {
						logger.debug("No metadata found, ignoring file");
						return;
					}
					new Thread(APIFactory.createFileDelete(file.getFileID())).start();
					logger.debug("Sent file delete request");
				}
			}

		} else if (delta.getKind() == IResourceDelta.ADDED) {
			if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {

				// do same as rename stuff
				IPath fullMovedFromPath = delta.getMovedFromPath();

				if (!fullMovedFromPath.toString().equals(iFile.getFullPath().toString())) {

					// Force close, to make sure changelistener doesn't fire.
					Path movedFromLocation = FSUtils
							.getLocationForRelativePath(delta.getMovedFromPath().toFile().toPath());
					ITextEditor editor = documentManager.getEditor(movedFromLocation);
					if (editor != null) {
						logger.debug("Closed editor for file " + movedFromLocation);
						editor.close(false);
					}

					if (iFile.getName().equals(fullMovedFromPath.lastSegment())) {
						// send File.Move request
						addedFileMove(fileLocation, movedFromLocation);
					} else {
						// send File.Rename request
						addedFileRename(iFile, fileLocation, movedFromLocation);
					}

				}
			} else {
				logger.debug(String.format("File added - %s", iFile.getName()));

				if (warnList.isFileInWarnList(fileLocation, FileCreateNotification.class)) {
					warnList.removeFileFromWarnList(fileLocation, FileCreateNotification.class);
				} else if (warnList.isFileInWarnList(fileLocation, FileCreateResponse.class)) {
					warnList.removeFileFromWarnList(fileLocation, FileCreateResponse.class);
				} else {
					createFile(iFile);
				}
			}
		}
	}

	private void changedFileRename(Path movedFromLocation, Path fileLocation, File file, String newName) {
		if (warnList.isFileInWarnList(fileLocation, FileRenameNotification.class)) {
			warnList.removeFileFromWarnList(fileLocation, FileRenameNotification.class);
		} else {
			new Thread(APIFactory.createFileRename(file.getFileID(), movedFromLocation, fileLocation, newName)).start();
			logger.debug(String.format("Sent file rename request; changing to %s", newName));
		}
	}

	private void changedFileMove(IResourceDelta delta, Path fileLocation) {
		File fileMeta;
		if (warnList.isFileInWarnList(fileLocation, FileMoveNotification.class)) {
			warnList.removeFileFromWarnList(fileLocation, FileMoveNotification.class);
		} else {
			// get metadata again but with the old path because
			// old one should be null
			// if the new path was used to find it
			Path movedToLocation = FSUtils
					.getLocationForRelativePath(delta.getMovedToPath().toFile().toPath());
			fileMeta = ss.getFile(movedToLocation);
			new Thread(APIFactory.createFileMove(fileMeta.getFileID(), fileLocation, movedToLocation)).start();
			logger.debug(String.format("Sent file move request; moving from %s to %s",
					fileLocation.toString(), movedToLocation));
		}
	}

	private void addedFileRename(IFile iFile, Path fileLocation, Path movedFromLocation) {
		File fileMeta;
		String newName = iFile.getProjectRelativePath().lastSegment();

		fileMeta = ss.getFile(movedFromLocation);
		if (fileMeta == null) {
			logger.debug("No metadata found, ignoring file");
			return;
		}

		if (warnList.isFileInWarnList(fileLocation, FileRenameNotification.class)) {
			warnList.removeFileFromWarnList(fileLocation, FileRenameNotification.class);
		} else {
			new Thread(APIFactory.createFileRename(fileMeta.getFileID(), movedFromLocation, fileLocation, newName)).start();
			logger.debug(String.format("Sent file rename request; changing to %s", newName));
		}
	}

	private void addedFileMove(Path fileLocation, Path movedFromLocation) {
		File fileMeta;
		if (warnList.isFileInWarnList(fileLocation, FileMoveNotification.class)) {
			warnList.removeFileFromWarnList(fileLocation, FileMoveNotification.class);
		} else {
			logger.debug(
					String.format("Getting metadata from file : %s", movedFromLocation.toString()));
			fileMeta = ss.getFile(movedFromLocation);
			if (fileMeta == null) {
				logger.warn("No metadata found, ignoring file");
				return;
			}

			new Thread(APIFactory.createFileMove(fileMeta.getFileID(), movedFromLocation, fileLocation)).start();
			logger.debug(String.format("Sent file move request; moving from %s to %s",
					movedFromLocation, fileLocation));
		}
	}

	private void createFile(IFile f) {
		Project pMeta = ss.getProject(f.getProject().getLocation().toFile().toPath());
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(pMeta.getName());
		CCIgnore ignoreFile = CCIgnore.createForProject(p);

		if (ignoreFile.containsEntry(f.getFullPath().toString())) {
			logger.debug(String.format("file ignored by .ccignore: %s", f.getFullPath().toString()));
			return;
		}

		try (InputStream in = f.getContents()) {
			byte[] fileBytes = FSUtils.inputStreamToByteArray(in);

			new Thread(APIFactory.createFileCreate(f.getName(), f.getLocation().toFile().toPath(), pMeta.getProjectID(),
					fileBytes)).start();

			logger.debug(String.format("Sent file create request: %s", f.getName()));
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		}
	}
}
