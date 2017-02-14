package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.file.IFileChangeExtension;

import patching.Patch;

public class FileChange extends AbstractAPICall {

	private long fileID;
	private Patch[] patches;

	public FileChange(ExtensionManager manager, long fileID, Patch[] patches) {
		this.extensions = manager.getExtensions(ExtensionIDs.FILE_CHANGE_ID);
		this.fileID = fileID;
		this.patches = patches;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
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
