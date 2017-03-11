package org.code.toboggan.filesystem.extensionpoints.file;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.patching.Patch;
import clientcore.websocket.models.File;

public interface IFSFileChangeExt extends ICoreExtension {
	public void fileChanged(long fileID, Patch patch);
}
