package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFilePullExtension;

public class FilePull extends AbstractAPICall {

	private long fileID;

	public FilePull(AbstractExtensionManager manager, long fileID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_PULL_DIFF_SEND_CHANGES_ID, IFilePullExtension.class);
		this.fileID = fileID;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IFilePullExtension pExt = (IFilePullExtension) e;
			pExt.filePulled(fileID);
		}
	}

	public long getFileID() {
		return fileID;
	}

}
