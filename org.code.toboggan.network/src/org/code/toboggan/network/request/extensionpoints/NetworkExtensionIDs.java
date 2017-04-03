package org.code.toboggan.network.request.extensionpoints;

import org.code.toboggan.network.NetworkActivator;

public class NetworkExtensionIDs {
	// ~~~ REQEUSTS ~~~
	// Projects
	public static final String PROJECT_CREATE_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.create";
	public static final String PROJECT_DELETE_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.delete";
	public static final String PROJECT_FETCH_SUBSCRIBE_ALL_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.fetchsubscribeall";
	public static final String PROJECT_GET_FILES_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.getfiles";
	public static final String PROJECT_GET_PERMISSIONS_CONST_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.getpermissionconstants";
	public static final String PROJECT_GRANT_PERMISSIONS_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.grantpermissions";
	public static final String PROJECT_LOOKUP_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.lookup";
	public static final String PROJECT_RENAME_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.rename";
	public static final String PROJECT_REVOKE_PERMISSIONS_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.revokepermissions";
	public static final String PROJECT_SUBSCRIBE_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.subscribe";
	public static final String PROJECT_UNSUBSCRIBE_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.project.unsubscribe";

	// Files
	public static final String FILE_CREATE_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.file.create";
	public static final String FILE_DELETE_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.file.delete";
	public static final String FILE_RENAME_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.file.rename";
	public static final String FILE_MOVE_REQUEST_ID = NetworkActivator.PLUGIN_ID + ".request.extensionpoints.file.move";
	public static final String FILE_CHANGE_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.file.change";
	public static final String FILE_PULL_REQUEST_ID = NetworkActivator.PLUGIN_ID + ".request.extensionpoints.file.pull";
	public static final String FILE_PULL_DIFF_SEND_CHANGES_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.file.pulldiffsendchanges";

	// Users
	public static final String USER_REGISTER_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.user.register";
	public static final String USER_LOGIN_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.user.login";
	public static final String USER_LOOKUP_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.user.lookup";
	public static final String USER_PROJECTS_REQUEST_ID = NetworkActivator.PLUGIN_ID
			+ ".request.extensionpoints.user.projects";

	// ~~~ NOTIFICATIONS ~~~
	// Files
	public static final String FILE_CREATE_NOTIFICATION_ID = NetworkActivator.PLUGIN_ID
			+ ".notification.extensionpoints.file.create";
	public static final String FILE_DELETE_NOTIFICATION_ID = NetworkActivator.PLUGIN_ID
			+ ".notification.extensionpoints.file.delete";
	public static final String FILE_MOVE_NOTIFICATION_ID = NetworkActivator.PLUGIN_ID
			+ ".notification.extensionpoints.file.move";
	public static final String FILE_RENAME_NOTIFICATION_ID = NetworkActivator.PLUGIN_ID
			+ ".notification.extensionpoints.file.rename";

	// Projects
	public static final String PROJECT_REVOKE_PERMISSIONS_NOTIFICATION_ID = NetworkActivator.PLUGIN_ID
			+ ".notification.extensionpoints.project.revokepermissions";
	public static final String PROJECT_RENAME_NOTIFICATION_ID = NetworkActivator.PLUGIN_ID
			+ ".notification.extensionpoints.project.rename";
	public static final String PROJECT_GRANT_PERMISSIONS_NOTIFICATION_ID = NetworkActivator.PLUGIN_ID
			+ ".notification.extensionpoints.project.grantpermissions";
	public static final String PROJECT_DELETE_NOTIFICATION_ID = NetworkActivator.PLUGIN_ID
			+ ".notification.extensionpoints.project.delete";

	// ~~~ WEBSOCKET ~~~
	public static final String WS_EVENT = NetworkActivator.PLUGIN_ID + ".ws.event";
}
