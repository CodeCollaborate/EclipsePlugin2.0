package org.code.toboggan.core.api.file;

import java.nio.file.Path;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFileCreateExtension;

public class FileCreate extends AbstractAPICall {

	private String name;
	private Path workspaceRelativePath;
	private long projectID;
	private byte[] fileBytes;
	
	public FileCreate(AbstractExtensionManager manager, String name, Path workspaceRelativePath, long projectID, byte[] fileBytes) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_CREATE_ID);
		this.name = name;
		this.workspaceRelativePath = workspaceRelativePath;
		this.projectID = projectID;
		this.fileBytes = fileBytes;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IFileCreateExtension pExt = (IFileCreateExtension) e;
			pExt.fileCreated(name, workspaceRelativePath, projectID, fileBytes);
		}
	}

	public String getName() {
		return name;
	}

	public Path getWorkspaceRelativePath() {
		return workspaceRelativePath;
	}

	public long getProjectID() {
		return projectID;
	}

	public byte[] getFileBytes() {
		return fileBytes;
	}

}
