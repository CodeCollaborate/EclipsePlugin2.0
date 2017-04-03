package org.code.toboggan.filesystem.editor.listeners;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.editor.DocumentManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Listens to changes in eclipse parts; notifies DocumentManager when new
 * documents/editors are opened or closed
 * 
 * @author Benedict
 */
public class EditorChangeListener extends AbstractEditorChangeListener {

	private final Logger logger = LogManager.getLogger("editorChangeListener");

	private DocumentChangeListener currListener = null;
	private final DocumentManager documentMgr = FSActivator.getDocumentManager();

	public EditorChangeListener() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editorRefs = activePage.getEditorReferences();
		for (IEditorReference ref : editorRefs) {
			IEditorPart editor = ref.getEditor(false);

			if (editor instanceof ITextEditor) {
				Path filePath = ((IFileEditorInput) editor.getEditorInput()).getFile().getLocation().toFile().toPath();
				this.documentMgr.openedEditor(filePath, (ITextEditor) editor);

				if (editor == activePage.getActiveEditor()) {
					this.partActivated(activePage.getActivePartReference());
				}
			}
		}
	}

	/**
	 * Called when a part is opened. Notifies documentManager of opened document
	 */
	@Override
	public void partOpened(IWorkbenchPartReference ref) {
		logger.debug(String.format("Part [%s] opened: [%s]", ref.getPartName(), ref.getTitle()));
		if (ref.getPart(false) instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor) ref.getPart(false);

			// editor is not a text editor, we don't support realtime changes
			// for it
			if (!(editor.getEditorInput() instanceof IFileEditorInput)) {
				return;
			}

			Path filePath = ((IFileEditorInput) editor.getEditorInput()).getFile().getLocation().toFile().toPath();

			this.documentMgr.openedEditor(filePath, editor);
			logger.debug(String.format("Editor opened [%s]", editor.getTitle()));
		}
	}

	/**
	 * Called when a part is closed. Notifies documentManager of document
	 * closure.
	 */
	@Override
	public void partClosed(IWorkbenchPartReference ref) {
		logger.debug(String.format("Part [%s] closed: [%s]", ref.getPartName(), ref.getTitle()));
		if (ref.getPart(false) instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor) ref.getPart(false);

			// editor is not an IFileEditorInput editor, we don't support
			// realtime changes for it
			if (!(editor.getEditorInput() instanceof IFileEditorInput)) {
				return;
			}

			IFile f = ((IFileEditorInput) editor.getEditorInput()).getFile();
			if (!f.exists()) {
				return;
			}
			Path filePath = f.getLocation().toFile().toPath();
			this.documentMgr.closedDocument(filePath);
			logger.debug(String.format("Editor closed [%s]", editor.getTitle()));
		}
	}

	/**
	 * Notify DocumentManager of active document, set new listener.
	 */
	@Override
	public void partActivated(IWorkbenchPartReference ref) {
		logger.debug(String.format("Part [%s] activated: [%s]", ref.getPartName(), ref.getTitle()));
		if (ref.getPart(false) instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor) ref.getPart(false);
			AbstractDocument document = (AbstractDocument) editor.getDocumentProvider()
					.getDocument(editor.getEditorInput());

			// editor is not a text editor, we don't support realtime changes
			// for it
			if (!(editor.getEditorInput() instanceof IFileEditorInput)) {
				return;
			}

			Path filePath = ((IFileEditorInput) editor.getEditorInput()).getFile().getLocation().toFile().toPath();

			if (!filePath.equals(this.documentMgr.getCurrFile())) {
				currListener = new DocumentChangeListener();

				this.documentMgr.setCurrFile(filePath);
				document.addDocumentListener(currListener);
			}
			logger.debug(String.format("Editor activated [%s]", editor.getTitle()));
		}
	}

	/**
	 * Notify DocumentManager of inactive document, removes listener
	 */
	@Override
	public void partDeactivated(IWorkbenchPartReference ref) {
		logger.debug(String.format("Part [%s] deactivated: [%s]", ref.getPartName(), ref.getTitle()));
		if (ref.getPart(false) instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor) ref.getPart(false);
			IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());

			if (currListener != null) {
				document.removeDocumentListener(currListener);
			}
			this.documentMgr.setCurrFile(null);

			logger.debug(String.format("Editor deactivated [%s]", editor.getTitle()));
		}
	}
}
