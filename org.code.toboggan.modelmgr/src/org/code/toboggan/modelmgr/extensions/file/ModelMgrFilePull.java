package org.code.toboggan.modelmgr.extensions.file;

import org.code.toboggan.filesystem.extensionpoints.file.IFSFilePullExt;
import org.eclipse.core.resources.IFile;

import clientcore.websocket.models.File;

public class ModelMgrFilePull extends AbstractFileModelMgrHandler implements IFSFilePullExt {
	@Override
	public void filePulled(File file, IFile iFile) {
		ss.getProject(file.getProjectID()).addFile(file);
		ss.setAbsoluteFilePath(iFile.getLocation().toFile().toPath(), file.getProjectID(), file.getFileID());
	}
}
