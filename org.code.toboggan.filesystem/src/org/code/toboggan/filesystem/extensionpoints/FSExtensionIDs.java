package org.code.toboggan.filesystem.extensionpoints;

import org.code.toboggan.filesystem.FSActivator;

public class FSExtensionIDs {
	// Projects
	public static final String PROJECT_CREATE_ID = FSActivator.PLUGIN_ID + ".project.create";
	public static final String PROJECT_RENAME_ID = FSActivator.PLUGIN_ID + ".project.rename";
	public static final String PROJECT_SUBSCRIBE_ID = FSActivator.PLUGIN_ID + ".project.subscribe";

	// Files
	public static final String FILE_CREATE_ID = FSActivator.PLUGIN_ID + ".file.create";
	public static final String FILE_DELETE_ID = FSActivator.PLUGIN_ID + ".file.delete";
	public static final String FILE_RENAME_ID = FSActivator.PLUGIN_ID + ".file.rename";
	public static final String FILE_MOVE_ID = FSActivator.PLUGIN_ID + ".file.move";
	public static final String FILE_CHANGE_ID = FSActivator.PLUGIN_ID + ".file.change";
	public static final String FILE_PULL_ID = FSActivator.PLUGIN_ID + ".file.pull";
}
