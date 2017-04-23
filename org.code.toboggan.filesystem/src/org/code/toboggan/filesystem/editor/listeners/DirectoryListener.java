package org.code.toboggan.filesystem.editor.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.filesystem.CCIgnore;
import org.code.toboggan.filesystem.utils.FSUtils;
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

		switch (delta.getKind()) {
		case IResourceDelta.REMOVED:
			// Project was renamed
			if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
				// If it's in the warn list, remove it and return;
				if (warnList.isProjectInWarnList(iProject.getName(), ProjectRenameNotification.class)) {
					warnList.removeProjectFromWarnList(iProject.getName(), ProjectRenameNotification.class);
					return true;
				}
				// Else, send a project rename request.
				else {
					String newName = delta.getMovedToPath().lastSegment();
					Path newPath = delta.getMovedToPath().makeAbsolute().toFile().toPath();

					APIFactory.createProjectRename(project.getProjectID(), newName, newPath).runAsync();
					logger.debug(String.format("Project renamed: new name: [%s]; new path: [%s]", newName, newPath));

					// Continue recursing, since children may be affected.
					return false;
				}
			} else {
				// If it's in the warn list, remove it and return;
				if (warnList.isProjectInWarnList(iProject.getName(), ProjectDeleteNotification.class)) {
					warnList.removeProjectFromWarnList(iProject.getName(), ProjectDeleteNotification.class);

					// No need to recurse; a project deletion automatically
					// deletes all files by definition.
					return true;
				} else {
					logger.debug(String.format("Project [%s] deleted on disk; unsubscribing", project.getName()));
					APIFactory.createProjectUnsubscribe(project.getProjectID()).runAsync();
					;

					// No need to recurse; we already are unsubscribed.
					return true;
				}
			}
		case IResourceDelta.ADDED:
			// If it's in the warn list, remove it and return;
			if (warnList.isProjectInWarnList(iProject.getName(), ProjectCreateResponse.class)) {
				warnList.removeProjectFromWarnList(iProject.getName(), ProjectCreateResponse.class);
			}

			return true;

		// Else, done. Return false
		default:
			return false;
		}

	}

	@Override
	protected void handleFile(IResourceDelta delta) {
		IFile iFile = (IFile) delta.getResource();
		Path newFileLocation = iFile.getLocation().toFile().toPath();
		File file = ss.getFile(newFileLocation);

		String kinds = "";
		String flags = "";
		Map<Integer, String> kindMap = new HashMap<>();
		Map<Integer, String> flagsMap = new HashMap<>();
		kindMap.put(IResourceDelta.ADDED, "ADDED");
		kindMap.put(IResourceDelta.ADDED_PHANTOM, "ADDED_PHANTOM");
		flagsMap.put(IResourceDelta.ALL_WITH_PHANTOMS, "ALL_WITH_PHANTOMS");
		kindMap.put(IResourceDelta.CHANGED, "CHANGED");
		flagsMap.put(IResourceDelta.CONTENT, "CONTENT");
		flagsMap.put(IResourceDelta.COPIED_FROM, "COPIED_FROM");
		flagsMap.put(IResourceDelta.DERIVED_CHANGED, "DERIVED_CHANGED");
		flagsMap.put(IResourceDelta.DESCRIPTION, "DESCRIPTION");
		flagsMap.put(IResourceDelta.ENCODING, "ENCODING");
		flagsMap.put(IResourceDelta.LOCAL_CHANGED, "LOCAL_CHANGED");
		flagsMap.put(IResourceDelta.MARKERS, "MARKERS");
		flagsMap.put(IResourceDelta.MOVED_FROM, "MOVED_FROM");
		flagsMap.put(IResourceDelta.MOVED_TO, "MOVED_TO");
		kindMap.put(IResourceDelta.NO_CHANGE, "NO_CHANGE");
		flagsMap.put(IResourceDelta.OPEN, "OPEN");
		kindMap.put(IResourceDelta.REMOVED, "REMOVED");
		kindMap.put(IResourceDelta.REMOVED_PHANTOM, "REMOVED_PHANTOM");
		flagsMap.put(IResourceDelta.REPLACED, "REPLACED");
		flagsMap.put(IResourceDelta.SYNC, "SYNC");
		flagsMap.put(IResourceDelta.TYPE, "TYPE");
		for (Entry<Integer, String> mask : kindMap.entrySet()) {
			if ((delta.getKind() & mask.getKey()) != 0) {
				kinds += mask.getValue() + ", ";
			}
		}
		for (Entry<Integer, String> mask : flagsMap.entrySet()) {
			if ((delta.getFlags() & mask.getKey()) != 0) {
				flags += mask.getValue() + ", ";
			}
		}
		logger.debug(String.format("File: [%s]; Kind flags: [%s]; File flags: [%s]", newFileLocation,
				kinds.substring(0, Math.max(0, kinds.length() - 2)),
				flags.substring(0, Math.max(0, flags.length() - 2))));

		if (delta.getKind() == IResourceDelta.CHANGED) {
			if (file == null) {
				// rather than deleting the document if metadata doesn't exist,
				// we want to
				// a) check it's not in the .ccignore
				// b) if it's not, add it to the server
				logger.info("No metadata found for file change event, creating");
				createFile(iFile);
			}

			if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
				IPath relativeMovedToPath = delta.getMovedToPath().removeFirstSegments(1);

				// TODO: What does this check do?
				if (!relativeMovedToPath.toString().equals(iFile.getProjectRelativePath().toString())) {
					Path absMovedFromPath = FSUtils
							.getLocationForRelativePath(delta.getMovedFromPath().toFile().toPath());

					logger.debug(String.format("File [%s] moved from [%s] to [%s]", file.getFilename(),
							absMovedFromPath.toString(), newFileLocation.toString()));

					if (iFile.getName().equals(relativeMovedToPath.lastSegment())) {
						// send File.Move request
						addedFileMove(newFileLocation, absMovedFromPath);
					} else {
						// send File.Rename request
						addedFileRename(newFileLocation, absMovedFromPath);
					}

					logger.debug(String.format("Updating documentManager path from [%s] to [%s]", absMovedFromPath,
							newFileLocation));
					documentManager.pathChangedForEditor(absMovedFromPath, newFileLocation);

				}

			} else if ((delta.getFlags() & IResourceDelta.CONTENT) != 0) {
				// don't diff this if this is the actively open file
				Path currFile = documentManager.getCurrFile();
				if (currFile != null) {
					if (currFile.equals(newFileLocation)) {
						logger.debug(String.format("File [%s] was changed, but is currently active document; ignoring",
								newFileLocation));
						return;
					}
				}

				if ((delta.getFlags() & IResourceDelta.REPLACED) != 0) {
					logger.debug(String.format("File [%s] had contents replaced, returning", newFileLocation));
					return;
				}

				if (warnList.isFileInWarnList(newFileLocation, FileChangeRequest.class)) {
					warnList.removeFileFromWarnList(newFileLocation, FileChangeRequest.class);
					logger.debug(String.format("Ignoring FileChangeRequest for file [%s]", newFileLocation));
				} else {
					if (file == null) {
						// file should have metadata but doesn't, so send a
						// request to make the file
						logger.debug(String.format("File with no metadata changed; creating file on server",
								newFileLocation));

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
						logger.debug(String.format("File [%s] changed in background - triggering pullDiffSendChanges",
								newFileLocation));

						APIFactory.createFilePullDiffSendChanges(file.getFileID()).runAsync();
					}
				}
			}

		} else if (delta.getKind() == IResourceDelta.REMOVED) {
			if ((delta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
				// File was deleted from disk
				if (warnList.isFileInWarnList(newFileLocation, FileDeleteNotification.class)) {
					warnList.removeFileFromWarnList(newFileLocation, FileDeleteNotification.class);
					logger.debug(String.format("Ignoring FileDeleteNotification for file [%s]", newFileLocation));
				} else {
					if (file == null) {
						logger.debug(String.format("File [%s] deleted, but was missing metadata; ignoring",
								newFileLocation));
						return;
					}
					APIFactory.createFileDelete(file.getFileID()).runAsync();
					logger.debug(String.format("File [%s] deleted - triggering a fileDeleteRequest", newFileLocation));
				}
			}
		} else if (delta.getKind() == IResourceDelta.ADDED) {
			if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
				IPath fullMovedFromPath = delta.getMovedFromPath();

				// TODO: What does this check do?
				if (!fullMovedFromPath.toString().equals(iFile.getFullPath().toString())) {
					logger.debug(String.format("File [%s] moved from [%s] to [%s]", iFile.getName(),
							fullMovedFromPath.makeAbsolute().toFile().toPath().toString(), newFileLocation.toString()));

					Path movedFromLocation = FSUtils
							.getLocationForRelativePath(delta.getMovedFromPath().toFile().toPath());

					if (iFile.getName().equals(fullMovedFromPath.lastSegment())) {
						// send File.Move request
						addedFileMove(newFileLocation, movedFromLocation);
					} else {
						// send File.Rename request
						addedFileRename(newFileLocation, movedFromLocation);
					}
				}
			} else {

				logger.debug(String.format("File [%s] added", newFileLocation.toString()));

				// Check if it's an ignored fileCreateNotification
				if (warnList.isFileInWarnList(newFileLocation, FileCreateNotification.class)) {
					warnList.removeFileFromWarnList(newFileLocation, FileCreateNotification.class);
					logger.debug(String.format("Ignoring FileCreateNotification for file [%s]", newFileLocation));
				}
				// Check if it's being created from a createResponse
				else if (warnList.isFileInWarnList(newFileLocation, FileCreateResponse.class)) {
					warnList.removeFileFromWarnList(newFileLocation, FileCreateResponse.class);
					logger.debug(String.format("Ignoring FileCreateResponse for file [%s]", newFileLocation));
				} else if (file != null) {
					logger.debug(
							"got FileCreate directory event for already-created file; diffing and sending changes");
					APIFactory.createFilePullDiffSendChanges(file.getFileID()).runAsync();
				} else {
					createFile(iFile);
				}
			}
		}
	}

	// private void changedFileRename(Path movedFromLocation, Path fileLocation,
	// File file, String newName) {
	// if (warnList.isFileInWarnList(fileLocation,
	// FileRenameNotification.class)) {
	// warnList.removeFileFromWarnList(fileLocation,
	// FileRenameNotification.class);
	// } else {
	// APIFactory.createFileRename(file.getFileID(), movedFromLocation,
	// fileLocation, newName).runAsync();
	// logger.debug(String.format("Sent file rename request; changing to [%s]",
	// newName));
	// }
	// }
	//
	// private void changedFileMove(IResourceDelta delta, Path fileLocation) {
	// File fileMeta;
	// if (warnList.isFileInWarnList(fileLocation, FileMoveNotification.class))
	// {
	// warnList.removeFileFromWarnList(fileLocation,
	// FileMoveNotification.class);
	// logger.debug(String.format("Ignoring FileMoveNotification for file [%s]",
	// fileLocation));
	// } else {
	// // get metadata again but with the old path because
	// // old one should be null
	// // if the new path was used to find it
	// Path movedToLocation =
	// FSUtils.getLocationForRelativePath(delta.getMovedToPath().toFile().toPath());
	// fileMeta = ss.getFile(movedToLocation);
	// APIFactory.createFileMove(fileMeta.getFileID(), fileLocation,
	// movedToLocation).runAsync();
	// logger.debug(String.format("Sent file move request; moving from [%s] to
	// [%s]", fileLocation.toString(),
	// movedToLocation));
	// }
	// }

	private void addedFileRename(Path fileLocation, Path movedFromLocation) {
		String newName = fileLocation.getFileName().toString();

		// If this was caused by a notification, the warn list should have an
		// entry keyed on the previous location
		if (warnList.isFileInWarnList(movedFromLocation, FileRenameNotification.class)) {
			warnList.removeFileFromWarnList(movedFromLocation, FileRenameNotification.class);
			logger.debug(String.format("Ignoring FileRenameNotification for file [%s]", fileLocation));
		} else {
			File fileMeta = ss.getFile(movedFromLocation);
			if (fileMeta == null) {
				logger.warn("No metadata found, ignoring file");
				return;
			}

			// Force close, to make sure changeListener is deregistered,
			// and re-registered upon the user opening the document
			// again
			// TODO: FIX THIS TO REMOVE THE NEED FOR CLOSING THE EDITOR
			ITextEditor editor = documentManager.getEditor(movedFromLocation);
			if (editor != null) {
				logger.debug("Closed editor for file " + movedFromLocation);
				editor.close(true);
			}

			APIFactory.createFileRename(fileMeta.getFileID(), movedFromLocation, fileLocation, newName).runAsync();
			logger.debug(String.format("Sent file rename request; changing to [%s]", newName));

			// logger.debug(String.format("Updating documentManager path from
			// [%s] to [%s]", movedFromLocation,
			// fileLocation));
			// documentManager.pathChangedForEditor(movedFromLocation,
			// fileLocation);
		}
	}

	private void addedFileMove(Path fileLocation, Path movedFromLocation) {
		// If this was caused by a notification, the warn list should have an
		// entry keyed on the previous location
		if (warnList.isFileInWarnList(movedFromLocation, FileMoveNotification.class)) {
			warnList.removeFileFromWarnList(movedFromLocation, FileMoveNotification.class);
		} else {
			logger.debug(String.format("Getting metadata from file : [%s]", movedFromLocation.toString()));
			File fileMeta = ss.getFile(movedFromLocation);
			if (fileMeta == null) {
				logger.warn("No metadata found, ignoring file");
				return;
			}

			// Force close, to make sure changeListener is deregistered,
			// and re-registered upon the user opening the document
			// again
			// TODO: FIX THIS TO REMOVE THE NEED FOR CLOSING THE EDITOR
			ITextEditor editor = documentManager.getEditor(movedFromLocation);
			if (editor != null) {
				logger.debug("Closed editor for file " + movedFromLocation);
				editor.close(true);
			}

			APIFactory.createFileMove(fileMeta.getFileID(), movedFromLocation, fileLocation).runAsync();
			logger.debug(
					String.format("Sent file move request; moving from [%s] to [%s]", movedFromLocation, fileLocation));

			// logger.debug(String.format("Updating documentManager path from
			// [%s] to [%s]", movedFromLocation,
			// fileLocation));
			// documentManager.pathChangedForEditor(movedFromLocation,
			// fileLocation);
		}
	}

	private void createFile(IFile f) {
		Project pMeta = ss.getProject(f.getProject().getLocation().toFile().toPath()); // What
																						// if
																						// this
																						// is
																						// null?
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(pMeta.getName());
		CCIgnore ignoreFile = CCIgnore.createForProject(p); // TODO: This reads
															// it from disk for
															// every single
															// call?

		if (ignoreFile.containsEntry(f.getFullPath().toString())) {
			logger.debug(String.format("file ignored by .ccignore: [%s]", f.getFullPath().toString()));
			return;
		}

		try (InputStream in = f.getContents()) {
			byte[] fileBytes = FSUtils.inputStreamToByteArray(in);

			APIFactory.createFileCreate(f.getName(), f.getLocation().toFile().toPath(), pMeta.getProjectID(), fileBytes)
					.runAsync();

			logger.debug(String.format("Sent file create request: [%s]", f.getName()));
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		}
	}
}
