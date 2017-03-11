package org.code.toboggan.modelmgr.extensions.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.network.request.extensionpoints.file.IFileChangeResponse;

import clientcore.patching.Patch;
import clientcore.websocket.models.File;

public class ModelMgrFileChange extends AbstractFileModelMgrHandler implements IFileChangeResponse {
	private Logger logger = LogManager.getLogger(ModelMgrFileChange.class);

	@Override
	public void fileChanged(long fileID, long fileVersion) {
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
	public void fileChangeFailed(long fileID, Patch[] patches) {
		// Do nothing
		
	}

}
