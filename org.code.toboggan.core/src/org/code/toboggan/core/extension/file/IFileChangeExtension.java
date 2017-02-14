package org.code.toboggan.core.extension.file;

import org.code.toboggan.core.extension.ICoreAPIExtension;
import patching.*;

public interface IFileChangeExtension extends ICoreAPIExtension {
	public void fileChanged(long fileID, Patch[] patches);
}
