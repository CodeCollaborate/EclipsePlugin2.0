package org.code.toboggan.core.api.file;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFileDeleteExtension;

public class FileDelete extends AbstractAPICall {

	private long fileID;

	public FileDelete(AbstractExtensionManager manager, long fileID) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_DELETE_ID, IFileDeleteExtension.class);
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
