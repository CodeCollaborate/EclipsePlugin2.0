package org.code.toboggan.core.api.file;

import java.nio.file.Path;

import org.code.toboggan.core.api.AbstractAPICall;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFileCreateExtension;

public class FileCreate extends AbstractAPICall {
	private String name;
	private Path absolutePath;
	private long projectID;
	private byte[] fileBytes;
	
	public FileCreate(AbstractExtensionManager manager, String name, Path absolutePath, long projectID, byte[] fileBytes) {
		this.extensions = manager.getExtensions(APIExtensionIDs.FILE_CREATE_ID, IFileCreateExtension.class);
		this.name = name;
		this.absolutePath = absolutePath;
		this.projectID = projectID;
		this.fileBytes = fileBytes;
	}

	@Override
	public void execute() {
		for (ICoreExtension e : this.extensions) {
			IFileCreateExtension pExt = (IFileCreateExtension) e;
			pExt.fileCreated(name, absolutePath, projectID, fileBytes);
		}
	}

	public String getName() {
		return name;
	}

	public Path getAbsolutePath() {
		return absolutePath;
	}

	public long getProjectID() {
		return projectID;
	}

	public byte[] getFileBytes() {
		return fileBytes;
	}

}
