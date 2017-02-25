package org.code.toboggan.ui.error.response;

import java.util.List;

import org.code.toboggan.network.request.extensionpoints.user.*;
import org.code.toboggan.ui.dialogs.MessageDialog;

import clientcore.websocket.models.Project;
import clientcore.websocket.models.User;

public class UserResponseErrorDisplay implements IUserLoginResponse, IUserLookupResponse, IUserProjectsResponse, IUserRegisterResponse {

	@Override
	public void userRegistered(String username) {
		// Do nothing
	}

	@Override
	public void userRegistrationFailed(String username, String firstName, String lastName, String email) {
		MessageDialog.createDialog("Failed to register as user " + username + ". Please reconnect to the server through the preferences and try again.").open();
	}

	@Override
	public void projectsRetrieved(List<Project> projects) {
		// Do nothing
	}

	@Override
	public void userProjectsFailed() {
		MessageDialog.createDialog("Failed to fetch projects from the server. Please reconnect to the server through the preferences.").open();
	}

	@Override
	public void userFound(User u) {
		// Do nothing
	}

	@Override
	public void userLookupFailed(String username) {
		MessageDialog.createDialog("Failed to lookup user " + username);
	}

	@Override
	public void loggedIn(String username) {
		// Do nothing
	}

	@Override
	public void loginFailed(String username) {
		MessageDialog.createDialog("Failed to login to CodeCollaborate. Please reconnect to the server through the preferences and try again.").open();
	}
}
