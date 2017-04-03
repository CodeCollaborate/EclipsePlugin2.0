package org.code.toboggan.filesystem.extensions.file;

import java.nio.file.Path;
import java.util.Date;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.extensionpoints.FSExtensionIDs;
import org.code.toboggan.filesystem.extensionpoints.file.IFSFileCreateExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.notification.extensionpoints.file.IFileCreateNotificationExtension;
import org.code.toboggan.network.request.extensionpoints.file.IFileCreateResponse;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.File;

public class FSFileCreate implements IFileCreateResponse, IFileCreateNotificationExtension {
	private Logger logger = LogManager.getLogger(FSFileCreate.class);

	private AbstractExtensionManager extMgr;
	private SessionStorage ss;

	public FSFileCreate() {
		this.extMgr = FileSystemExtensionManager.getInstance();
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
			logger.error(String.format("Create notification for [%s] had version 0.", file.getFilename()));
			return;
		}

		logger.debug("Received file create notification");

		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.FILE_CREATE_ID, IFSFileCreateExt.class);
		for (ICoreExtension ext : extensions) {
			IFSFileCreateExt createExt = (IFSFileCreateExt) ext;
			createExt.fileCreated(projectID, file);
		}

		APIFactory.createFilePull(file.getFileID()).runAsync();
	}

	@Override
	public void fileCreated(long fileID, String name, Path absolutePath, String projectRelativePath, long projectID,
			String fileContents) {
		File file = new File(fileID, name, projectRelativePath, 1, ss.getUsername(), new Date().toString()); // doing
																												// date
																												// like
																												// this
																												// is
																												// questionable,
																												// but
																												// it's
																												// not
																												// being
																												// used

		FSActivator.getShadowDocumentManager().putShadow(fileID, fileContents);

		Set<ICoreExtension> extensions = extMgr.getExtensions(FSExtensionIDs.FILE_CREATE_ID, IFSFileCreateExt.class);
		for (ICoreExtension ext : extensions) {
			IFSFileCreateExt createExt = (IFSFileCreateExt) ext;
			createExt.fileCreated(projectID, file);
		}

	}

	@Override
	public void fileCreateFailed(String fileName, Path absolutePath, long projectID, byte[] fileBytes) {
		// do nothing
	}

}
