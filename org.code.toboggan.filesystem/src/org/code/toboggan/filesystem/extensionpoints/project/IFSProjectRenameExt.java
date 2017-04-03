package org.code.toboggan.filesystem.extensionpoints.project;

public interface IFSProjectRenameExt {
	void projectRenamed(long projectID, String newName);

	void projectRenamedFailed(long projectID);
}
