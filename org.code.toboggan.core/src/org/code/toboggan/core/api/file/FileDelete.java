package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFileDeleteExtension;

public class FileDelete extends AbstractAPICall {

	private long fileID;
	
	public FileDelete(AbstractExtensionManager manager, long fileID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_DELETE_ID);
		this.fileID = fileID;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IFileDeleteExtension pExt = (IFileDeleteExtension) e;
			pExt.fileDeleted(fileID);
		}
	}

	public long getFileID() {
		return fileID;
	}

}
