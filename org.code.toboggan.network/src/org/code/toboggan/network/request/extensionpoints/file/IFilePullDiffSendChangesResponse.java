package org.code.toboggan.network.request.extensionpoints.file;

public interface IFilePullDiffSendChangesResponse {
	public void filePulled(byte[] fileBytes);
	public void filePullFailed(long fileID);
}
