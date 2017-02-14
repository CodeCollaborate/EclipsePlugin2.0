package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.file.IFilePullExtension;

public class FilePull extends AbstractAPICall {

	private long fileID;

	public FilePull(ExtensionManager manager, long fileID) {
		this.extensions = manager.getExtensions(ExtensionIDs.FILE_PULL_ID);
		this.fileID = fileID;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IFilePullExtension pExt = (IFilePullExtension) e;
			pExt.filePulled(fileID);
		}
	}

	public long getFileID() {
		return fileID;
	}

}
