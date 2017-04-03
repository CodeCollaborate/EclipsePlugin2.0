package org.code.toboggan.network.request.extensionpoints.file;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.patching.Patch;

public interface IFileChangeResponse extends ICoreExtension {
	public void fileChanged(long fileID, Patch patches, Patch[] missingPatches, long fileVersion);

	public void fileChangeFailed(long fileID, Patch patches);
}
