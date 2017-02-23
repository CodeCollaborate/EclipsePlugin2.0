package org.code.toboggan.filesystem.extensionpoints.file;

import org.code.toboggan.core.extension.ICoreExtension;
import org.eclipse.core.resources.IFile;

import clientcore.websocket.models.File;

public interface IFSFileCreateExt extends ICoreExtension {
	public void fileCreated(File file,  IFile iFile);
}
