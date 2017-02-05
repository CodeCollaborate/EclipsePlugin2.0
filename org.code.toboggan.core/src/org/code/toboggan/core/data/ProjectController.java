package org.code.toboggan.core.data;

import java.util.HashMap;

import dataMgmt.*;
import websocket.models.*;

public class ProjectController {
	SessionStorage storage;
	
	public ProjectController(SessionStorage storage) {
		this.storage = storage;
	}
	
	public Project createProject(long id, String name, HashMap<String, Permission> permission) {
		Project p = new Project(id, name, permission);
		storage.setProject(p);
		return p;
	}
}
