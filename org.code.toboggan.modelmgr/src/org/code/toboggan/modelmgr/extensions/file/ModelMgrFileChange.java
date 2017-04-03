package org.code.toboggan.modelmgr.extensions.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.filesystem.extensionpoints.file.IFSFileChangeExt;

import clientcore.patching.Patch;
import clientcore.websocket.models.File;

public class ModelMgrFileChange extends AbstractFileModelMgrHandler implements IFSFileChangeExt {
	private Logger logger = LogManager.getLogger(ModelMgrFileChange.class);

	@Override
	public void fileChangeSuccess(long fileID, long fileVersion) {
		File fileMetadata = ss.getFile(fileID);
		if (fileVersion == 0) {
			logger.error("File version returned from server was 0");
		} else {
			synchronized (fileMetadata) {
				fileMetadata.setFileVersion(fileVersion);
			}
		}
	}

	@Override
	public void fileChangedOnDisk(long fileID, Patch patch) {
		// Do nothing

	}

}
