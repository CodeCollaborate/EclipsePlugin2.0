package org.code.toboggan.network.request.extensionpoints.file;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IFileDeleteResponse extends ICoreExtension {
	public void fileDeleted(long fileID);
	public void fileDeleteFailed(long fileID);
}
