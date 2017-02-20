package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFilePullDiffSendChangesExtension;

public class FilePullDiffSendChanges extends AbstractAPICall {

	private long fileID;

	public FilePullDiffSendChanges(AbstractExtensionManager manager, long fileID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_PULL_ID);
		this.fileID = fileID;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IFilePullDiffSendChangesExtension pExt = (IFilePullDiffSendChangesExtension) e;
			pExt.filePulled(fileID);
		}
	}

	public long getFileID() {
		return fileID;
	}

}
