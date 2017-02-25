package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFileChangeExtension;

import clientcore.patching.Patch;

public class FileChange extends AbstractAPICall {

	private long fileID;
	private Patch[] patches;

	public FileChange(AbstractExtensionManager manager, long fileID, Patch[] patches) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_CHANGE_ID, IFileChangeExtension.class);
		this.fileID = fileID;
		this.patches = patches;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IFileChangeExtension pExt = (IFileChangeExtension) e;
			pExt.fileChanged(fileID, patches);
		}
	}

	public long getFileID() {
		return fileID;
	}

	public Patch[] getPatches() {
		return patches;
	}

}
