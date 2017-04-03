package org.code.toboggan.modelmgr.extensions.file;

import java.nio.file.Path;

import org.code.toboggan.filesystem.extensionpoints.file.IFSFileDeleteExt;
import org.code.toboggan.network.request.extensionpoints.file.IFileDeleteResponse;

public class ModelMgrFileDelete extends AbstractFileModelMgrHandler implements IFileDeleteResponse, IFSFileDeleteExt {

	@Override
	public void fileDeleted(long fileID) {
		fc.deleteFile(fileID);
	}

	@Override
	public void fileDeleteFailed(long fileID) {
		// Do nothing
	}

	@Override
	public void fileOpenInEditor(long fileID, Path fileLocation) {
		// Do nothing

	}

	@Override
	public void deleteFailed(long fileID, Path fileLocation) {
		// Do nothing

	}

}
