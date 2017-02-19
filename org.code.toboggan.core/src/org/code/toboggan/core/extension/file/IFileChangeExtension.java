package org.code.toboggan.core.extension.file;

import org.code.toboggan.core.extension.ICoreExtension;
import clientcore.patching.*;

public interface IFileChangeExtension extends ICoreExtension {
	public void fileChanged(long fileID, Patch[] patches);
}
