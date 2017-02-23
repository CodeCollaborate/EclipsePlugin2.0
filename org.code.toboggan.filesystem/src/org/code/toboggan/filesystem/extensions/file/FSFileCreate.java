package org.code.toboggan.filesystem.extensions.file;

import java.nio.file.Path;
import java.util.Date;
import java.util.Set;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.filesystem.extensionpoints.file.IFSFileCreateExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.network.request.extensionpoints.file.IFileCreateResponse;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;

import clientcore.dataMgmt.FileController;
import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.File;

public class FSFileCreate implements IFileCreateResponse {

	private SessionStorage ss;
	private FileController fc;
	private AbstractExtensionManager extMgr;
	
	public FSFileCreate() {
		this.ss = CoreActivator.getSessionStorage();
		this.fc = new FileController(ss);
		this.extMgr = FileSystemExtensionManager.getInstance();
	}
	
	@Override
	public void fileCreated(long fileID, String name, Path absolutePath, String projectRelativePath, long projectID) {		
		// NOTE: This is only here for now so that I don't forget to include it when making extensions in the model plugin
		File f = new File(fileID, name, projectRelativePath, 1, ss.getUsername(), new Date().toString()); // doing date like this is questionable, but it's not being used
		ss.setFile(projectID, f);
		fc.putFileLocation(absolutePath, projectID, fileID);
		
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new org.eclipse.core.runtime.Path(absolutePath.toString()));
		
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_CREATE_ID);
		for (ICoreExtension e : extensions) {
			IFSFileCreateExt createExt = (IFSFileCreateExt) e;
			createExt.fileCreated(f, iFile);
		}
	}

	@Override
	public void fileCreateFailed(String fileName, Path absolutePath, long projectID, byte[] fileBytes) {
		// Do nothing
	}

}
