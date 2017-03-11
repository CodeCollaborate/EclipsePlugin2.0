package org.code.toboggan.modelmgr.extensions.project;

import org.code.toboggan.network.request.extensionpoints.project.IProjectUnsubscribeResponse;

public class ModelMgrProjectUnsubscribe extends AbstractProjectModelMgrHandler implements IProjectUnsubscribeResponse {

	@Override
	public void unsubscribed(long projectID) {
		ss.setUnsubscribed(projectID);
	}

	@Override
	public void unsubscribeFailed(long projectID) {
		// Do nothing
	}
	
}
