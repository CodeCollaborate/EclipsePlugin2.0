package org.code.toboggan.filesystem.editor.listeners;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.editor.DocumentManager;
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
import clientcore.websocket.ConnectException;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.responses.FileChangeResponse;

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
		ss = null; // TODO: get from core plugin
		pm = null; // TODO: get from ??
	}
	
	/**
	 * Called when document is about to be changed.
	 * 
	 * @param event
	 *            the DocumentEvent that triggered this listener.
	 */
	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		System.out.printf("DocumentChangeListener %s got event %s", this, event.toString());

		List<Diff> diffs = new ArrayList<>();
		String currDocument = event.getDocument().get();

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

		if (file.getFileVersion() == 0) {
			logger.error("File version was 0");
		}

		// Create removal diffs if needed
		if (event.getLength() > 0) {
			Diff diff = new Diff(false, event.getOffset(),
					currDocument.substring(event.getOffset(), event.getOffset() + event.getLength()));
			diffs.add(diff);
		}
		// Create insertion diffs if needed
		if (!event.getText().isEmpty()) {
			Diff patch = new Diff(true, event.getOffset(), event.getText());
			diffs.add(patch);
		}

		synchronized (((SynchronizableDocument)event.getDocument()).getLockObject()) {
			// If diffs were not incoming, applied diffs, convert to LF
			List<Diff> newDiffs = new ArrayList<>();
			diffLoop: for (int i = 0; i < diffs.size(); i++) {
				LinkedList<Diff> appliedDiffs = docMgr.getAppliedDiffs(currFile);
				synchronized (appliedDiffs) {
					int offset = 0;
					// Find first diff that matches, if any.
					for (int j = 0; j < appliedDiffs.size(); j++) {
						Diff appliedDiff = appliedDiffs.get(j);
						System.out.printf("isNotification: %s ?= %s; %b\n", diffs.get(i).toString(),
								appliedDiff.toString(), diffs.get(i).equals(appliedDiff));
						// If found matching diff, remove all previous diffs.
						if (appliedDiff.equals(diffs.get(i))) {
							for (int k = j-offset; k >= 0; k--) {
								appliedDiffs.removeFirst();
								offset++;
							}
							continue diffLoop;
						}
					}
				}
				newDiffs.add(diffs.get(i).convertToLF(currDocument));
			}

			// If no diffs left; abort
			if (newDiffs.isEmpty()) {
				System.out.println("No new diffs, aborting.");
				return;
			}

			// Create the patch
			Patch patch = new Patch(file.getFileVersion(), newDiffs);

			System.out.println("DocumentManager sending change request, with patch " + patch.toString());

            try {
                pm.sendPatch(file.getFileID(),
                        new Patch[] { patch }, response -> {
                            synchronized (file) {
                                long version = ((FileChangeResponse) response.getData()).getFileVersion();
                                if (version == 0) {
                                	logger.error("File version returned from server was 0");
                                }
                                file.setFileVersion(version);
                            }
                        }, null);
            } catch (ConnectException e) {
            	logger.error("Failed to send change request", e);
            }
        }
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
		Path fileLocation = iFile.getLocation().toFile().toPath();
		File file = ss.getFile(fileLocation);
		if (proj == null || file == null || !ss.getSubscribedIds().contains(proj.getProjectID())
				|| file.getFilename().contains(CoreStringConstants.CONFIG_FILE_NAME)) {
			return;
		}
		System.out.println("DocumentChange-NewModificationStamp: " + ((SynchronizableDocument) event.getDocument()).getModificationStamp());
		pm.setModificationStamp(file.getFileID(), ((SynchronizableDocument) event.getDocument()).getModificationStamp());
	}
}
