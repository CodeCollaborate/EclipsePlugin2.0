package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.ExtensionIDs;
import org.code.toboggan.core.extension.ExtensionManager;
import org.code.toboggan.core.extension.ICoreAPIExtension;
import org.code.toboggan.core.extension.file.IFileDeleteExtension;

public class FileDelete extends AbstractAPICall {

	private long fileID;
	
	public FileDelete(ExtensionManager manager, long fileID) {
		this.extensions = manager.getExtensions(ExtensionIDs.FILE_DELETE_ID);
		this.fileID = fileID;
	}

	@Override
	public void execute() {
		for (ICoreAPIExtension e : this.extensions) {
			IFileDeleteExtension pExt = (IFileDeleteExtension) e;
			pExt.fileDeleted(fileID);
		}
	}

	public long getFileID() {
		return fileID;
	}

}
