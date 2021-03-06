package org.code.toboggan.filesystem.editor;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.WarnList;
import org.code.toboggan.network.NetworkActivator;
import org.eclipse.core.internal.filebuffers.SynchronizableDocument;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import clientcore.dataMgmt.SessionStorage;
import clientcore.patching.Diff;
import clientcore.patching.Patch;
import clientcore.patching.PatchManager;
import clientcore.websocket.IFileChangeNotificationHandler;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Notification;
import clientcore.websocket.models.notifications.FileChangeNotification;
import clientcore.websocket.models.requests.FileChangeRequest;

/**
 * Manages documents, finds editors as needed.
 *
 * @author Benedict
 *
 */
@SuppressWarnings("restriction") // for import of SynchronizableDocument
public class DocumentManager implements IFileChangeNotificationHandler {

	private final Logger logger = LogManager.getLogger("documentManager");

	private SessionStorage ss;
	private PatchManager pm;
	private final WarnList warnList;

	private Path currFile = null;
	private HashMap<Path, ITextEditor> openEditors = new HashMap<>();
	private HashMap<Path, LinkedList<Diff>> appliedDiffs = new HashMap<>();

	public DocumentManager() {
		ss = CoreActivator.getSessionStorage();
		pm = NetworkActivator.getPatchManager();
		warnList = FSActivator.getWarnList();
	}

	/**
	 * Gets file path of current file
	 *
	 * @return current file's path
	 */
	public Path getCurrFile() {
		return currFile;
	}

	/**
	 * Path of file that is currently active/open
	 *
	 * @param absolutePath
	 *            File path of active file
	 */
	public void setCurrFile(Path absolutePath) {
		if (absolutePath == null) {
			this.currFile = null;
			return;
		}
		this.currFile = absolutePath;
	}

	/**
	 * FilePath of editor that was just opened
	 *
	 * @param absolutePath
	 *            File path of opened editor
	 * @param editor
	 *            Editor that is opened for the given file
	 */
	public void openedEditor(Path absolutePath, ITextEditor editor) {
		this.openEditors.put(absolutePath, editor);

		IFile file = editor.getEditorInput().getAdapter(IFile.class);
		Path fullPath = file.getLocation().toFile().toPath();
		File fileMeta = ss.getFile(fullPath);
		if (fileMeta == null) {
			return;
		}

		pm.setModificationStamp(fileMeta.getFileID(),
				((SynchronizableDocument) editor.getDocumentProvider().getDocument(editor.getEditorInput()))
						.getModificationStamp());
	}

	/**
	 * Call when a document is closed.
	 *
	 * @param absolutePath
	 *            filePath of file that was closed.
	 */
	public void closedDocument(Path absolutePath) {
		if (absolutePath == null || absolutePath.equals(this.currFile)) {
			setCurrFile(null);
		}
		this.openEditors.remove(absolutePath);
		this.sendFilePullRequest(absolutePath);
	}

	private void sendFilePullRequest(Path absolutePath) {
		File file = ss.getFile(absolutePath);

		if (file == null) {
			logger.warn(String.format("Closed an untracked file: [%s]", absolutePath));
			return;
		}
		long projId = file.getProjectID();
		Set<Long> subProjIds = ss.getSubscribedIds();
		if (!subProjIds.contains(projId)) {
			logger.debug("Closed a file in an unsubscribed project");
			return;
		}

		APIFactory.createFilePull(file.getFileID()).runAsync();
		// TODO: make sure this code is used in implementation of network
		// FilePull extension
		// Request req = (new
		// FilePullRequest(file.getFileID())).getRequest(response -> {
		// if (response.getStatus() == 200) {
		// byte[] fileBytes = ((FilePullResponse)
		// response.getData()).getFileBytes();
		// String fileContents = new String(fileBytes);
		// List<Patch> patches = new ArrayList<>();
		// for (String stringPatch : ((FilePullResponse)
		// response.getData()).getChanges()) {
		// patches.add(new Patch(stringPatch));
		// }
		// IFile newFile =
		// ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);
		// newFile = newFile.getProject().getFile(file.getFilePath());
		// fileContents =
		// pm.getDataManager().getPatchManager().applyPatch(fileContents,
		// patches);
		//
		// NullProgressMonitor progressMonitor = new NullProgressMonitor();
		// try {
		// if (newFile.exists()) {
		// pm.putFileInWarnList(newFile.getFullPath().makeAbsolute().toString(),
		// FileChangeResponse.class);
		// ByteArrayInputStream in = new
		// ByteArrayInputStream(fileContents.getBytes());
		// newFile.setContents(in, false, false, progressMonitor);
		// in.close();
		// } else {
		// // warn directory watching before creating the file
		// pm.putFileInWarnList(newFile.getFullPath().makeAbsolute().toString(),
		// FileCreateResponse.class);
		// ByteArrayInputStream in = new
		// ByteArrayInputStream(fileContents.getBytes());
		// newFile.create(in, false, progressMonitor);
		// in.close();
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// Display.getDefault().asyncExec(() -> MessageDialog
		// .createDialog("Failed to pull file on resource close. Please
		// resubscribe to the project."));
		// }
		// }
		// }, new UIRequestErrorHandler("Couldn't send file pull request after
		// file close: " + absolutePath));
		// pm.getWSManager().sendAuthenticatedRequest(req);
	}

	/**
	 * Gets the editor for the given file
	 *
	 * @param absolutePath
	 *            path of file that is open
	 * @return ITextEditor instance for given filePath
	 */
	public ITextEditor getEditor(Path absolutePath) {
		return this.openEditors.get(absolutePath);
	}

	/**
	 * Gets the editor for the given file
	 *
	 * @param absolutePath
	 *            path of file that is open
	 * @return ITextEditor instance for given filePath
	 */
	public AbstractDocument getDocument(Path absolutePath) {
		return getDocumentForEditor(this.openEditors.get(absolutePath));
	}

	/**
	 * Updates the path key for a given editor
	 * 
	 * @param oldAbsolutePath
	 *            the old absolute path for the IDocument
	 * @param newAbsolutePath
	 *            the new absolute path for the IDocument
	 */
	public void pathChangedForEditor(Path oldAbsolutePath, Path newAbsolutePath) {
		ITextEditor editor = this.openEditors.remove(oldAbsolutePath);
		this.openEditors.put(newAbsolutePath, editor);
	}

	/**
	 * Get the queue of diffs that were just applied for the given filepath.
	 *
	 * @return The queue of diffs that was applied for the given filepath.
	 */
	public LinkedList<Diff> getAppliedDiffs(Path filepath) {
		if (!appliedDiffs.containsKey(filepath)) {
			appliedDiffs.put(filepath, new LinkedList<>());
		}
		return appliedDiffs.get(filepath);
	}

	/**
	 * Gets the active document for a given editor
	 *
	 * @param editor
	 *            the editor to retrieve the document from
	 * @return the document which was retrieved.
	 */
	private AbstractDocument getDocumentForEditor(ITextEditor editor) {
		if (editor == null) {
			return null;
		}

		IDocumentProvider provider = editor.getDocumentProvider();
		IEditorInput input = editor.getEditorInput();
		if (provider != null && input != null) {
			return (AbstractDocument) editor.getDocumentProvider().getDocument(editor.getEditorInput());
		} else {
			logger.error("Error getting document for editor");
			return null;
		}
	}

	/**
	 * Notification handler for document manager. Parses generic notification to
	 * FileChangeNotification.
	 *
	 * @param n
	 *            Notification of file changes.
	 */
	@Override
	public Long handleNotification(Notification n, Patch[] originalPatches, Long expectedModificationStamp) {
		// Convert to correct notification types
		FileChangeNotification changeNotif = (FileChangeNotification) n.getData();

		// Parse list of patches.
		Patch patch = new Patch(changeNotif.changes);

		// Get file path to write to.
		File file = ss.getFile(n.getResourceID());
		if (file == null) {
			// Early out if no such file object found.
			return -1l;
		}
		Long projectID = file.getProjectID();

		Path projectRootPath = ss.getProjectLocation(projectID);
		Path absolutePath = Paths.get(projectRootPath.toString(), file.getRelativePath().toString(),
				file.getFilename());

		// TODO(wongb): Build patch reorder buffer, making sure that they are
		// applied in
		// order.
		// This is a temporary fix.
		// if (changeNotif.fileVersion <= fileMeta.getVersion()) {
		// try {
		// logger.debug(String.format(
		// "ChangeNotification version was less than or equal to current
		// version: [%d] <= [%d]; Notification: ",
		// changeNotif.fileVersion, fileMeta.getVersion(), new
		// ObjectMapper().writeValueAsString(n)));
		// } catch (JsonProcessingException e) {
		// e.printStackTrace();
		// }
		// return;
		// }

		Long result = this.applyPatch(n.getResourceID(), expectedModificationStamp, absolutePath, patch);

		if (result != null) {
			synchronized (file) {
				file.setFileVersion(changeNotif.fileVersion);

				try {
					String newContent = pm.applyPatch(
							FSActivator.getShadowDocumentManager().getShadow(n.getResourceID()),
							Arrays.asList(originalPatches));
					// Update shadow
					FSActivator.getShadowDocumentManager().putShadow(n.getResourceID(), newContent);
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionAsString = sw.toString();

					logger.error(String.format(
							"DocumentManager - Error applying notification patches [%s] to document shadow: %s",
							Arrays.toString(originalPatches), exceptionAsString));
				}
			}
		}
		return result;
	}

	/**
	 * If the document is open, patch it in memory. Otherwise, send it back to
	 * client core for file patching.
	 *
	 * @param fileId
	 *            fileId to patch; this is mainly used for passing to clientCore
	 * @param fileLocation
	 *            absolute file path; used as key in editorMap, and patches.
	 * @param workspaceRelativePath
	 *            workspace relative file path that includes the filename
	 * @param patches
	 *            the list of patches to apply, in order.
	 */
	public Long applyPatch(long fileId, long expectedModificationStamp, Path fileLocation, Patch change) {
		Path currFile = this.currFile;
		ITextEditor editor = getEditor(fileLocation);

		// Get reference to open document
		AbstractDocument document = getDocumentForEditor(editor);

		Long[] result = new Long[1];

		if (editor != null && document != null) {

			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					synchronized (((SynchronizableDocument) document).getLockObject()) {
						// If modification stamp does not match,
						if (expectedModificationStamp != -1
								&& document.getModificationStamp() != expectedModificationStamp) {
							logger.debug(
									"Document changed between notification arrival and attempt to append. Retrying");
							logger.debug("Got modification stamp " + document.getModificationStamp() + "; wanted "
									+ expectedModificationStamp);
							result[0] = null;
							return;
						}

						// Get text in document.
						String newDocument = document.get();

						// If CRLFs are found, apply patches in CRLF mode.
						boolean useCRLF = newDocument.contains("\r\n");
						Patch patch = change;

						if (useCRLF) {
							patch = patch.convertToCRLF(newDocument);
						}

						Diff prevDiff = null;
						int index = 0;
						List<Diff> diffs = patch.getDiffs();

						for (Diff diff : diffs) {
							// Throw errors if we are trying to insert
							// between
							// \r and \n
							if (diff.getStartIndex() > 0 && diff.getStartIndex() < document.get().length()
									&& document.get().charAt(diff.getStartIndex() - 1) == '\r'
									&& document.get().charAt(diff.getStartIndex()) == '\n') {
								throw new IllegalArgumentException("Tried to insert between \\r and \\n");
							}

							// First diff should have as many noOps as it's
							// start index
							int noOpLength = diff.getStartIndex();
							// for subsequent diffs, use the distance between
							// last edited part
							if (prevDiff != null) {
								if (prevDiff.isInsertion() || prevDiff.getStartIndex() == diff.getStartIndex()) {
									noOpLength = diff.getStartIndex() - prevDiff.getStartIndex();
								} else {
									if (prevDiff.getStartIndex() + prevDiff.getLength() > diff.getStartIndex()) {
										throw new IllegalArgumentException(
												"Attempted to modify diff within range of previous deletion");
									}
									noOpLength = diff.getStartIndex()
											- (prevDiff.getStartIndex() + prevDiff.getLength());
								}
							}

							index += noOpLength;
							Diff offsetDiff = diff.clone();
							offsetDiff.setStartIndex(index);

							// Add to appliedDiffs list to ignore on
							// DocumentChangeListener side
							if (currFile != null && currFile.equals(fileLocation)) {
								if (!appliedDiffs.containsKey(fileLocation)) {
									appliedDiffs.put(fileLocation, new LinkedList<>());
								}
								appliedDiffs.get(fileLocation).add(offsetDiff);
							}

							// Apply the change to the document
							try {
								logger.debug(
										String.format("Attempting to apply diff [%s]; currentModificationStamp [%d]",
												offsetDiff, document.getModificationStamp()).replace("\n", "\\n"));
								if (diff.isInsertion()) {
									document.replace(offsetDiff.getStartIndex(), 0, diff.getChanges());
									index += diff.getLength();
								} else {
									// Check deleted text is same as in diff
									String currContents = document.get();
									try {
										String deletedText = currContents.substring(offsetDiff.getStartIndex(),
												offsetDiff.getStartIndex() + diff.getLength());
										if (!deletedText.equals(diff.getChanges())) {
											throw new IllegalStateException(String
													.format("Deleted text [%s] does not match contents of diff [%s]",
															deletedText, diff.getChanges())
													.replace("\n", "\\n"));
										}
									} catch (Exception e) {
										logger.error(String.format(
												"Exception thrown in looking for deleted text of range [%d]:[%d] in document [%d]",
												offsetDiff.getStartIndex(),
												offsetDiff.getStartIndex() + diff.getLength(), currContents.length()));
									}

									// Replace the given text with the empty
									// string.
									document.replace(offsetDiff.getStartIndex(), diff.getLength(), "");

									// Index doesn't change; the text block is
									// already deleted
								}

							} catch (BadLocationException e) {
								logger.error(String.format("Bad Location; Patch: [%s], Len: [%d], Text: [%s]\n",
										diff.toString(), document.get().length(), document.get()), e);
							}

							// Update previous diff
							prevDiff = diff;
						}
						result[0] = document.getModificationStamp();
					}
				}
			});

			Display.getDefault().asyncExec(() -> {
				warnList.putFileInWarnList(fileLocation, FileChangeRequest.class);
				editor.doSave(new NullProgressMonitor());
			});

			return result[0];
		} else {
			// If file is not open in an editor, enqueue the patch for
			// writing.

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath ipath = new org.eclipse.core.runtime.Path(fileLocation.toString());
			IFile file = workspace.getRoot().getFileForLocation(ipath);
			if (!file.exists()) {
				logger.warn(String.format("Cannot apply patches to non-existent file: [%s]", fileLocation.toString()));
				return -1l;
			}

			String contents = null;
			try (Scanner s = new Scanner(file.getContents())) {
				contents = s.useDelimiter("\\A").hasNext() ? s.next() : "";
			} catch (CoreException e) {
				logger.error("Cannot read file", e);
				return -1l;
			}
			String newContents = pm.applyPatch(contents, Arrays.asList(change));
			warnList.putFileInWarnList(fileLocation, FileChangeRequest.class);
			try {
				file.setContents(new ByteArrayInputStream(newContents.getBytes()), true, true, null);
			} catch (CoreException e) {
				logger.error("Fail to update files on disk", e);
				return -1l;
			}

			return -1l;
		}
	}
}
