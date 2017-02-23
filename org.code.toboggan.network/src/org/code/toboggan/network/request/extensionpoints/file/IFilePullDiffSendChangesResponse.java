package org.code.toboggan.network.request.extensionpoints.file;

public interface IFilePullDiffSendChangesResponse {
	public void filePulled(long fileID, byte[] fileBytes, String[] changes);
	public void filePullFailed(long fileID);
}
