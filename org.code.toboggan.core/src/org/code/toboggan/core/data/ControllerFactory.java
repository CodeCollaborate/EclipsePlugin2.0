package org.code.toboggan.core.data;

import dataMgmt.DataManager;
import dataMgmt.SessionStorage;

public class ControllerFactory {
	// TODO: change to get session storage properly after clientcore refactor
	private static final SessionStorage STORAGE = DataManager.getInstance().getSessionStorage();
	
	public static ProjectController createProjectController() {
		return new ProjectController(STORAGE);
	}
}
