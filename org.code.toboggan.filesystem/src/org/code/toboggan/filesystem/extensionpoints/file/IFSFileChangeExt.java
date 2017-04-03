package org.code.toboggan.filesystem.extensionpoints.file;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.patching.Patch;

public interface IFSFileChangeExt extends ICoreExtension {
	public void fileChangeSuccess(long fileID, long fileVersion);

	public void fileChangedOnDisk(long fileID, Patch patch);
}
