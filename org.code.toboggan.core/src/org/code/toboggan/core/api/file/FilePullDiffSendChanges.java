package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFilePullDiffSendChangesExtension;

public class FilePullDiffSendChanges extends AbstractAPICall {

	private long fileID;

	public FilePullDiffSendChanges(AbstractExtensionManager manager, long fileID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_PULL_DIFF_SEND_CHANGES_ID, IFilePullDiffSendChangesExtension.class);
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
