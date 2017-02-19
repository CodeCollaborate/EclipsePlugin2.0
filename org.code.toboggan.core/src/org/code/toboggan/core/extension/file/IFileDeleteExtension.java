package org.code.toboggan.core.extension.file;

import org.code.toboggan.core.extension.ICoreExtension;

public interface IFileDeleteExtension extends ICoreExtension {
	public void fileDeleted(long fileID);
}
