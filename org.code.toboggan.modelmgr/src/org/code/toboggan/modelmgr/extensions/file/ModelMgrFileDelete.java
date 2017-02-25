package org.code.toboggan.modelmgr.extensions.file;

import org.code.toboggan.network.notification.extensionpoints.file.IFileDeleteNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.file.IFileDeleteResponse;

public class ModelMgrFileDelete extends AbstractFileModelMgrHandler implements IFileDeleteResponse, IFileDeleteNotificationExtension {

	@Override
	public void fileDeleted(long fileID) {
		fc.deleteFile(fileID);
	}

	@Override
	public void fileDeleteNotification(long deletedId) {
		fc.deleteFile(deletedId);
	}

	@Override
	public void fileDeleteFailed(long fileID) {
		// Do nothing
	}

	
}
