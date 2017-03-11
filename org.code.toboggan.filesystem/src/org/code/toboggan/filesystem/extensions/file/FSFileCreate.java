package org.code.toboggan.filesystem.extensions.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.network.notification.extensionpoints.file.IFileCreateNotificationExtension;
import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.File;

public class FSFileCreate implements IFileCreateNotificationExtension {
	private Logger logger = LogManager.getLogger(FSFileCreate.class);
	
	private SessionStorage ss;
	
	public FSFileCreate() {
		this.ss = CoreActivator.getSessionStorage();
	}

	@Override
	public void fileCreateNotification(long projectID, File file) {
		file.setProjectID(projectID);
		
		if (ss.getFile(file.getFileID()) != null) {
			logger.warn("Received file create notification for an already-existent file");
			return;
		}

		if (file.getFileVersion() == 0) {
			logger.error(String.format("Create notification for %s had version 0.", file.getFilename()));
			return;
		}
		
		logger.debug("Received file create notification");
		APIFactory.createFilePull(file.getFileID()).runAsync();
	}

}
