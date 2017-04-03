package org.code.toboggan.filesystem.extensions.file;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
import org.code.toboggan.filesystem.extensionpoints.file.IFSFilePullExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.NetworkActivator;
import org.code.toboggan.network.request.extensionpoints.file.IFilePullResponse;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.texteditor.ITextEditor;

import clientcore.dataMgmt.SessionStorage;
import clientcore.patching.Patch;
import clientcore.patching.PatchManager;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.requests.FileChangeRequest;

public class FSFilePull implements IFilePullResponse {
	private static Logger logger = LogManager.getLogger(FSFilePull.class);

	private SessionStorage ss;
	private WarnList warnList;
	private PatchManager pm;
	private DocumentManager dm;
	private AbstractExtensionManager extMgr;

	public FSFilePull() {
		this.ss = CoreActivator.getSessionStorage();
		this.warnList = FSActivator.getWarnList();
		this.pm = NetworkActivator.getPatchManager();
		this.dm = FSActivator.getDocumentManager();
		this.extMgr = FileSystemExtensionManager.getInstance();
	}

	@Override
	public void filePulled(long fileID, byte[] fileBytes, String[] changes) {
		// Assumption: Project.GetFiles or File.CreateNotification has already
		// been run, therefore metadata has already been inserted.
		File file = ss.getFile(fileID);
		Project project = ss.getProject(file.getProjectID());
		IPath relPath = new org.eclipse.core.runtime.Path(file.getRelativePath().toString());
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());

		if (!createFolders(file, relPath, p)) {
			return;
		}

		relPath = relPath.append(file.getFilename());
		logger.debug(String.format("Making file [%s]", relPath.toString()));
		IFile newFile = p.getFile(relPath);
		Path fileLocation = newFile.getLocation().toFile().toPath();

		createFiles(fileBytes, changes, file, newFile, fileLocation);

		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.FILE_PULL_ID, IFSFilePullExt.class);
		for (ICoreExtension e : extensions) {
			IFSFilePullExt createExt = (IFSFilePullExt) e;
			createExt.filePulled(file, newFile);
		}
	}

	private boolean createFolders(File file, IPath relPath, IProject p) {
		NullProgressMonitor progressMonitor = new NullProgressMonitor();

		logger.debug(String.format("Processing path [%s]", relPath.toString()));
		if (!relPath.toString().equals("") && !relPath.toString().equals(".")) {

			IPath currentFolder = org.eclipse.core.runtime.Path.EMPTY;
			for (int i = 0; i < relPath.segmentCount(); i++) {
				// iterate through path segments and create if they don't exist
				currentFolder = currentFolder.append(relPath.segment(i));
				logger.debug(String.format("Making folder [%s]", currentFolder.toString()));

				IFolder newFolder = p.getFolder(currentFolder);
				try {
					if (!newFolder.exists()) {
						newFolder.create(true, true, progressMonitor);
					}
				} catch (Exception e1) {
					logger.error(
							String.format("Could not create folder for [%s], unsubscribing", currentFolder.toString()),
							e1);
					APIFactory.createProjectUnsubscribe(file.getProjectID()).runAsync();
					return false;
				}
			}
		}
		return true;
	}

	private void createFiles(byte[] fileBytes, String[] changes, File file, IFile newFile, Path fileLocation) {
		NullProgressMonitor progressMonitor = new NullProgressMonitor();

		try {
			// apply patches
			String fileContents = new String(fileBytes);
			List<Patch> patches = new ArrayList<>();
			for (String stringPatch : changes) {
				patches.add(new Patch(stringPatch));
			}
			fileContents = pm.applyPatch(fileContents, patches);

			FSActivator.getShadowDocumentManager().putShadow(file.getFileID(), fileContents);

			if (newFile.exists()) {
				// Force close, to make sure changelistener doesn't fire.
				ITextEditor editor = dm.getEditor(fileLocation);
				if (editor != null) {
					logger.debug("Closed editor for file " + fileLocation.toString());
					// Don't need to save, since we are removing the file
					editor.close(false);
				}

				warnList.putFileInWarnList(fileLocation, FileChangeRequest.class);
				ByteArrayInputStream in = new ByteArrayInputStream(fileContents.getBytes());
				newFile.setContents(in, false, false, progressMonitor);
				in.close();
			} else {
				// warn directory watching before creating the file
				warnList.putFileInWarnList(fileLocation, FileChangeRequest.class);
				ByteArrayInputStream in = new ByteArrayInputStream(fileContents.getBytes());
				newFile.create(in, false, progressMonitor);
				in.close();
			}

			if (file.getFileVersion() == 0) {
				System.err.println(String.format("File [%s] was pulled with version 0.", file.getFilename()));
			}
		} catch (Exception e) {
			logger.error("Error pulling file, unsubscribing", e);
			APIFactory.createProjectUnsubscribe(file.getProjectID()).runAsync();
			// TODO: notify UI
			return;
		}
	}

	@Override
	public void filePullFailed(long fileID) {
		// TODO Error handling
	}

}
