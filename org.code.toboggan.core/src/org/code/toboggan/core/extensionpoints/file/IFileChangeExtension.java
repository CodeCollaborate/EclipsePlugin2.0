package org.code.toboggan.core.extensionpoints.file;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import clientcore.patching.Patch;

public interface IFileChangeExtension extends ICoreExtension {
	public void fileChanged(long fileID, Patch[] patches);
}
