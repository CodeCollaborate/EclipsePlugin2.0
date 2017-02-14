package org.code.toboggan.core.extension.file;

import org.code.toboggan.core.extension.ICoreAPIExtension;

public interface IFilePullExtension extends ICoreAPIExtension {
	public void filePulled(long fileID);
}
