package org.code.toboggan.modelmgr.extensions.file;

import org.code.toboggan.core.CoreActivator;

import clientcore.dataMgmt.FileController;
import clientcore.dataMgmt.SessionStorage;

public abstract class AbstractFileModelMgrHandler {
	protected SessionStorage ss;
	protected FileController fc;
	
	public AbstractFileModelMgrHandler() {
		this.ss = CoreActivator.getSessionStorage();
		this.fc = new FileController(ss);
	}
}
