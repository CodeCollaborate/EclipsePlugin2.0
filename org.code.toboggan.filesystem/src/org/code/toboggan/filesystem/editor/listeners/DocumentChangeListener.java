package org.code.toboggan.filesystem.editor.listeners;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.editor.DocumentManager;
import org.code.toboggan.network.NetworkActivator;
import org.eclipse.core.internal.filebuffers.SynchronizableDocument;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.texteditor.ITextEditor;

import clientcore.constants.CoreStringConstants;
import clientcore.dataMgmt.SessionStorage;
import clientcore.patching.Diff;
import clientcore.patching.Patch;
import clientcore.patching.PatchManager;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;

/**
 * Listens for document changes, and dispatches a new FileChangeRequest when
 * changes occur.
 * 
 * @author Benedict
 */
@SuppressWarnings("restriction")
public class DocumentChangeListener implements IDocumentListener {

	private final Logger logger = LogManager.getLogger("documentChangeListener");

	private DocumentManager docMgr;
	private PatchManager pm;
	private SessionStorage ss;

	public DocumentChangeListener() {
		docMgr = FSActivator.getDocumentManager();
		ss = CoreActivator.getSessionStorage();
		pm = NetworkActivator.getPatchManager();
		logger.debug("New documentChangeListener created");
	}

	// Can be kept here, since this class can only ever be fired from the main thread; thus
	// it will always be called in documentAboutToBeChange -> documentChanged
	private String fileContents;

	/**
	 * Called when document is about to be changed.
	 * 
	 * @param event
	 *            the DocumentEvent that triggered this listener.
	 */
	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		logger.debug(String.format("DocumentChangeListener [%s] got event [%s]", this, event.toString()));

		String currDocument = event.getDocument().get();
		this.fileContents = currDocument;
	}

	/**
	 * No nothing, simple stub.
	 * 
	 * @param event
	 *            The DocumentEvent that triggered this listener.
	 */
	@Override
	public void documentChanged(DocumentEvent event) {
		Path currFile = docMgr.getCurrFile();
		ITextEditor editor = docMgr.getEditor(currFile);
		IFile iFile = editor.getEditorInput().getAdapter(IFile.class);
		IProject iProj = iFile.getProject();
		Project proj = ss.getProject(iProj.getLocation().toFile().toPath());
		Path fullPath = iFile.getLocation().toFile().toPath();
		File file = ss.getFile(fullPath);
		if (proj == null || file == null || !ss.getSubscribedIds().contains(proj.getProjectID())
				|| file.getFilename().contains(CoreStringConstants.CONFIG_FILE_NAME)) {
			return;
		}
		List<Diff> diffs = new ArrayList<>();

		// If document is not in a dirty state (not a user-initiated change,
		// trigger PullFileDiffSendChanges, and don't send this change.
		if (!editor.getDocumentProvider().canSaveDocument(editor.getEditorInput())) {
			APIFactory.createFilePullDiffSendChanges(file.getFileID()).runAsync();
			return;
		}

		if (file.getFileVersion() == 0) {
			logger.error("File version was 0");
		}

		// Create removal diffs if needed
		if (event.getLength() > 0) {
			Diff diff = new Diff(false, event.getOffset(),
					fileContents.substring(event.getOffset(), event.getOffset() + event.getLength()));
			diffs.add(diff);
		}
		// Create insertion diffs if needed
		if (!event.getText().isEmpty()) {
			Diff patch = new Diff(true, event.getOffset(), event.getText());
			diffs.add(patch);
		}

		synchronized (((SynchronizableDocument) event.getDocument()).getLockObject()) {
			// If diffs were not incoming, applied diffs, convert to LF
			List<Diff> newDiffs = new ArrayList<>();
			diffLoop: for (int i = 0; i < diffs.size(); i++) {
				LinkedList<Diff> appliedDiffs = docMgr.getAppliedDiffs(currFile);
				synchronized (appliedDiffs) {
					int offset = 0;
					// Find first diff that matches, if any.
					for (int j = 0; j < appliedDiffs.size(); j++) {
						Diff appliedDiff = appliedDiffs.get(j);
						logger.debug(String.format("isNotification: [%s] ?= [%s]; %b\n", diffs.get(i).toString(),
								appliedDiff.toString(), diffs.get(i).equals(appliedDiff)));
						// If found matching diff, remove all previous diffs.
						if (appliedDiff.equals(diffs.get(i))) {
							for (int k = j - offset; k >= 0; k--) {
								appliedDiffs.removeFirst();
								offset++;
							}
							continue diffLoop;
						}
					}
				}
				newDiffs.add(diffs.get(i));
			}

			// If no diffs left; abort
			if (newDiffs.isEmpty()) {
				logger.debug("No new diffs, aborting.");
				return;
			}

			// Create the patch
			Patch patch = new Patch(file.getFileVersion(), newDiffs, fileContents.length());

			logger.debug(String.format("DocumentManager sending change request, with patch [%s]", patch.toString()));
			
			// Must be run synchronously, since the batching queue must be updated before document modificationStamps
			APIFactory.createFileChange(file.getFileID(), new Patch[] { patch }, fileContents).run();

			logger.debug("DocumentChange-NewModificationStamp: "
					+ ((SynchronizableDocument) event.getDocument()).getModificationStamp());
			pm.setModificationStamp(file.getFileID(),
					((SynchronizableDocument) event.getDocument()).getModificationStamp());
		}

		// Nullify local fileContents.
		this.fileContents = null;
	}
}
