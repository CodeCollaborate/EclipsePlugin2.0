package org.code.toboggan.network.request.extensionpoints.file;

import clientcore.patching.Patch;

public interface IFileChangeResponse {
	public void fileChanged(long fileID, long fileVersion);
	public void fileChangeFailed(long fileID, Patch[] patches);
}
