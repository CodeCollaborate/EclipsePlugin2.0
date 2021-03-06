package org.code.toboggan.core.extensionpoints;

import org.code.toboggan.core.CoreActivator;

public class APIExtensionIDs {
	// Projects
	public static final String PROJECT_CREATE_ID = CoreActivator.PLUGIN_ID + ".project.create";
	public static final String PROJECT_DELETE_ID = CoreActivator.PLUGIN_ID + ".project.delete";
	public static final String PROJECT_RENAME_ID = CoreActivator.PLUGIN_ID + ".project.rename";
	public static final String PROJECT_GET_FILES_ID = CoreActivator.PLUGIN_ID + ".project.getfiles";
	public static final String PROJECT_GET_PERMISSIONS_CONST_ID = CoreActivator.PLUGIN_ID
			+ ".project.getpermissionconstants";
	public static final String PROJECT_GRANT_PERMISSIONS_ID = CoreActivator.PLUGIN_ID + ".project.grantpermissions";
	public static final String PROJECT_LOOKUP_ID = CoreActivator.PLUGIN_ID + ".project.lookup";
	public static final String PROJECT_REVOKE_PERMISSIONS_ID = CoreActivator.PLUGIN_ID + ".project.revokepermissions";
	public static final String PROJECT_SUBSCRIBE_ID = CoreActivator.PLUGIN_ID + ".project.subscribe";
	public static final String PROJECT_UNSUBSCRIBE_ID = CoreActivator.PLUGIN_ID + ".project.unsubscribe";
	public static final String PROJECT_FETCH_SUBSCRIBE_ALL_ID = CoreActivator.PLUGIN_ID + ".project.fetchsubscribeall";
	public static final String PROJECT_FETCH_ALL = CoreActivator.PLUGIN_ID + ".project.fetchall";

	// Files
	public static final String FILE_CREATE_ID = CoreActivator.PLUGIN_ID + ".file.create";
	public static final String FILE_DELETE_ID = CoreActivator.PLUGIN_ID + ".file.delete";
	public static final String FILE_RENAME_ID = CoreActivator.PLUGIN_ID + ".file.rename";
	public static final String FILE_MOVE_ID = CoreActivator.PLUGIN_ID + ".file.move";
	public static final String FILE_CHANGE_ID = CoreActivator.PLUGIN_ID + ".file.change";
	public static final String FILE_PULL_ID = CoreActivator.PLUGIN_ID + ".file.pull";
	public static final String FILE_PULL_DIFF_SEND_CHANGES_ID = CoreActivator.PLUGIN_ID + ".file.pulldiffsendchanges";

	// Users
	public static final String USER_REGISTER_ID = CoreActivator.PLUGIN_ID + ".user.register";
	public static final String USER_LOGIN_ID = CoreActivator.PLUGIN_ID + ".user.login";
	public static final String USER_LOOKUP_ID = CoreActivator.PLUGIN_ID + ".user.lookup";
	public static final String USER_PROJECTS_ID = CoreActivator.PLUGIN_ID + ".user.projects";

	// Other
	public static final String STORAGE_LISTENER_ID = CoreActivator.PLUGIN_ID + ".storagelistener";
}
