package org.code.toboggan.filesystem.extensions.file;

import java.util.Arrays;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.extensionpoints.FSExtensionIDs;
import org.code.toboggan.filesystem.extensionpoints.file.IFSFileChangeExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.NetworkActivator;
import org.code.toboggan.network.request.extensionpoints.file.IFileChangeResponse;

import clientcore.dataMgmt.SessionStorage;
import clientcore.patching.Patch;
import clientcore.websocket.models.File;

public class FSFileChange implements IFileChangeResponse {
	private Logger logger = LogManager.getLogger(FSFileChange.class);

	private AbstractExtensionManager extMgr;
	private SessionStorage ss;

	public FSFileChange() {
		this.extMgr = FileSystemExtensionManager.getInstance();
		this.ss = CoreActivator.getSessionStorage();
	}

	@Override
	public void fileChanged(long fileID, Patch changes, Patch[] missingPatches, long fileVersion) {
		File file = ss.getFile(fileID);
		if (file == null) {
			logger.error("Missing file for ID: " + fileID);
		}

		String shadowContent = FSActivator.getShadowDocumentManager().getShadow(fileID);

		// Update shadow document in a try-catch all block, to make sure
		// metadata is updated properly
		try {
			// Then apply new patches
			shadowContent = NetworkActivator.getPatchManager().applyPatch(shadowContent, Arrays.asList(changes));

			// Update shadow & version
			FSActivator.getShadowDocumentManager().putShadow(fileID, shadowContent);
		} catch (Exception e) {
			logger.error(String.format("FSFileChange failed in applying returned change [%s] to shadow document",
					changes.toString()), e);
		}

		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.FILE_CHANGE_ID, IFSFileChangeExt.class);
		for (ICoreExtension ext : extensions) {
			IFSFileChangeExt changeExt = (IFSFileChangeExt) ext;
			changeExt.fileChangeSuccess(fileID, fileVersion);
		}

	}

	@Override
	public void fileChangeFailed(long fileID, Patch patches) {
		// Do nothing
	}

}
