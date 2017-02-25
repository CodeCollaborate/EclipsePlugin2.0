package org.code.toboggan.modelmgr.extensions.project;

import org.code.toboggan.core.CoreActivator;

import clientcore.dataMgmt.ProjectController;
import clientcore.dataMgmt.SessionStorage;

public abstract class AbstractProjectModelMgrHandler {
	protected SessionStorage ss;
	protected ProjectController pc;
	
	public AbstractProjectModelMgrHandler() {
		this.ss = CoreActivator.getSessionStorage();
		this.pc = new ProjectController(ss);
	}
}
