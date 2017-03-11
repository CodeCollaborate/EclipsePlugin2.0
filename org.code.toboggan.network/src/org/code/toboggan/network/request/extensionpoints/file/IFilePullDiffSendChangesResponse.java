package org.code.toboggan.network.request.extensionpoints.file;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IFilePullDiffSendChangesResponse extends ICoreExtension {
	public void filePulled(long fileID, byte[] fileBytes, String[] changes);
	public void filePullFailed(long fileID);
}
