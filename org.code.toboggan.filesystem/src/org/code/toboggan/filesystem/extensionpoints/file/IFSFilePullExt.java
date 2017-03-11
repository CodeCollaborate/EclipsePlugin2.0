package org.code.toboggan.filesystem.extensionpoints.file;

import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.eclipse.core.resources.IFile;

import clientcore.websocket.models.File;

public interface IFSFilePullExt extends ICoreExtension {
	public void filePulled(File file, IFile iFile);
}
