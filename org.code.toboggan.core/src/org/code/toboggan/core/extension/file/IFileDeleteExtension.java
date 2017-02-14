package org.code.toboggan.core.extension.file;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IFileDeleteExtension extends ICoreAPIExtension {
	public void fileDeleted(long fileID);
}
