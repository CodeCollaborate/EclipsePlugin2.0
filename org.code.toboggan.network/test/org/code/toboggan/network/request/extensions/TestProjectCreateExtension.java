package org.code.toboggan.network.request.extensions;

import org.code.toboggan.core.extension.APIExtensionManager;
import org.junit.Before;
import org.junit.Test;

import dataMgmt.DataManager;
import dataMgmt.SessionStorage;
import websocket.WSManager;
import websocket.models.Project;

public class TestProjectCreateExtension {
	
	private SessionStorage storage;
	private WSManager wsMgr;
	private APIExtensionManager extMgr;
	
	@Before
	public void setup() {
		DataManager dataMgr = new DataManager();
		storage = dataMgr.getSessionStorage();
		// TODO: make mock wsmanager
	}
	
	@Test
	public void testSuccessfulCreation() {
		ProjectCreateExtension ext = new ProjectCreateExtension(storage, wsMgr, extMgr);
		Project project = new Project(-1, "name", null); // TODO: change to new project constructor
		ext.projectCreated(project);
		// TODO: add mocks to test req logic
	}
	
	@Test
	public void testFailCreate() {
		// TODO: implement when can mock
	}
	
	@Test
	public void testFailSubscribe() {
		// TODO: implement when can mock
	}
}
