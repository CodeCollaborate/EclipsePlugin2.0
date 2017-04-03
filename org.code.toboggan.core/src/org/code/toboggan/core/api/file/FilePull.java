package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFilePullExtension;

public class FilePull extends AbstractAPICall {

	private long fileID;

	public FilePull(AbstractExtensionManager manager, long fileID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_PULL_ID, IFilePullExtension.class);
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
