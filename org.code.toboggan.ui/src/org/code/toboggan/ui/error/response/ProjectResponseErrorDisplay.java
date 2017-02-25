package org.code.toboggan.ui.error.response;

import java.nio.file.Path;
import java.util.List;

import org.code.toboggan.network.request.extensionpoints.project.*;
import org.code.toboggan.ui.dialogs.MessageDialog;
import com.google.common.collect.BiMap;

import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;

public class ProjectResponseErrorDisplay implements IProjectCreateResponse, IProjectDeleteResponse, IProjectFetchAllResponse, IProjectFetchAndSubscribeAllResponse,
		IProjectGetFilesResponse, IProjectGetPermissionConstantsResponse, IProjectLookupResponse, IProjectRenameResponse, IProjectRevokePermissionsResponse,
		IProjectSubscribeResponse, IProjectUnsubscribeResponse
{

	@Override
	public void unsubscribed(long projectID) {
		// Do nothing
	}

	@Override
	public void unsubscribeFailed(long projectID) {
		MessageDialog.createDialog("Error unsubscribing from project. Please retry unsubscribing.").open();
	}

	@Override
	public void permissionsRevoked(long projectID, String username) {
		// Do nothing
	}

	@Override
	public void permissionsRevokeFailed(long projectID, String username) {
		MessageDialog.createDialog("Error revoking permissions for " + username + ". Please verify that you have a higher permission level than them.").open();
	}

	@Override
	public void projectRenameFailed(long projectID, String newName) {
		MessageDialog.createDialog("Error renaming project to " + newName + ". Please try again.").open();
	}

	@Override
	public void projectFound(List<Project> projects) {
		// Do nothing
	}

	@Override
	public void projectLookupFailed(List<Long> projectIDs) {
		MessageDialog.createDialog("Error fetching project. Please try again.").open();
	}

	@Override
	public void getPermissionConstants(BiMap<String, Byte> permConstants) {
		// Do nothing
	}

	@Override
	public void getPermissionConstantsFailed() {
		MessageDialog.createDialog("Error fetching permission data. Please reconnect to the server through the preferences.").open();
	}

	@Override
	public void projectGetFilesFailed(long projectID) {
		MessageDialog.createDialog("Failed to get files from the server. Please resubscribe to the project to try again.").open();
	}

	@Override
	public void fetchedAll(List<Project> projects) {
		// Do nothing
	}

	@Override
	public void fetchAllFailed() {
		MessageDialog.createDialog("Failed to fetch projects from the server. Please reconnect to the server through the preferences.").open();
	}

	@Override
	public void projectDeleted(long projectID) {
		// Do nothing
	}

	@Override
	public void projectDeleteFailed(long projectID) {
		MessageDialog.createDialog("Failed to delete project from server. Please refresh the projects list and try again.").open();
	}

	@Override
	public void projectCreated(long projectId) {
		// Do nothing
	}

	@Override
	public void projectCreationFailed(String name) {
		MessageDialog.createDialog("Failed to create project " + name + " on server. Please try again.").open();
	}
	
	@Override
	public void subscribed(long projectId) {
		// Do nothing
	}
	
	@Override
	public void subscribed(long projectID, List<File> files) {
		// Do nothing
	}

	@Override
	public void subscribeFailed(long projectId) {
		MessageDialog.createDialog("Failed to subscribe to project. Please reconnect to the server in the preferences and try again.").open();
	}

	@Override
	public void projectFetched(Project p) {
		// Do nothing
	}

	@Override
	public void projectFetchFailed(long projectId) {
		MessageDialog.createDialog("Failed to fetch project from the server. Please try again.").open();
	}

	@Override
	public void projectRenamed(long projectID, String newName, Path newProjectLocation) {
		// Do nothing		
	}

	@Override
	public void projectGetFiles(long projectID, File[] files) {
		// Do nothing
	}
}
