package org.code.toboggan.filesystem.extensionpoints.file;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.websocket.models.File;

public interface IFSFileCreateExt extends ICoreExtension {
	public void fileCreated(long projectID, File file);
}
