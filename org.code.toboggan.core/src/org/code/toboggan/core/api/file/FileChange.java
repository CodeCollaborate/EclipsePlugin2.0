package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFileChangeExtension;

import clientcore.patching.Patch;

public class FileChange extends AbstractAPICall {

	private long fileID;
	private Patch[] patches;
	private String fileContents;

	public FileChange(AbstractExtensionManager manager, long fileID, Patch[] patches, String fileContents) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_CHANGE_ID, IFileChangeExtension.class);
		this.fileID = fileID;
		this.patches = patches;
		this.fileContents = fileContents;
	}

	@Override
	public void execute() {
		for(int i = 0; i < patches.length; i++){
			patches[i] = patches[i].convertToLF(fileContents);
		}
		
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
