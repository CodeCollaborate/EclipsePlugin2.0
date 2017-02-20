package org.code.toboggan.network.request.extensionpoints.file;

public interface IFileDeleteResponse {
	public void fileDeleted(long fileID);
	public void fileDeleteFailed(long fileID);
}
