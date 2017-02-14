package org.code.toboggan.core.extension;

import org.code.toboggan.core.Activator;

public class ExtensionIDs {
	// Projects
	public static final String PROJECT_CREATE_ID = Activator.PLUGIN_ID + ".project.create";
	public static final String PROJECT_DELETE_ID = Activator.PLUGIN_ID + ".project.delete";
	public static final String PROJECT_RENAME_ID = Activator.PLUGIN_ID + ".project.rename";
	public static final String PROJECT_GET_FILES_ID = Activator.PLUGIN_ID + ".project.getFiles";
	public static final String PROJECT_GET_PERMISSIONS_CONST_ID = Activator.PLUGIN_ID + ".project.getPermissionsContants";
	public static final String PROJECT_GRANT_PERMISSIONS_ID = Activator.PLUGIN_ID + ".project.grantPermissions";
	public static final String PROJECT_LOOKUP_ID = Activator.PLUGIN_ID + ".project.lookup";
	public static final String PROJECT_REVOKE_PERMISSIONS_ID = Activator.PLUGIN_ID + ".project.revokePermissions";
	public static final String PROJECT_SUBSCRIBE_ID = Activator.PLUGIN_ID + ".project.subscribe";
	public static final String PROJECT_UNSUBSCRIBE_ID = Activator.PLUGIN_ID + ".project.unsubscribe";

	// Files
	public static final String FILE_CREATE_ID = Activator.PLUGIN_ID + ".file.create";
	public static final String FILE_DELETE_ID = Activator.PLUGIN_ID + ".file.delete";
	public static final String FILE_RENAME_ID = Activator.PLUGIN_ID + ".file.rename";
	public static final String FILE_MOVE_ID = Activator.PLUGIN_ID + ".file.move";
	public static final String FILE_CHANGE_ID = Activator.PLUGIN_ID + ".file.change";
	public static final String FILE_PULL_ID = Activator.PLUGIN_ID + ".file.pull";
	
	// Users
	public static final String USER_REGISTER_ID = Activator.PLUGIN_ID + ".user.register";
	public static final String USER_LOGIN_ID = Activator.PLUGIN_ID + ".user.login";
	public static final String USER_LOOKUP_ID = Activator.PLUGIN_ID + ".user.lookup";
	public static final String USER_PROJECTS_ID = Activator.PLUGIN_ID + ".user.projects";
}
