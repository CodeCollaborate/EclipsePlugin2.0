package org.code.toboggan.network.request.extensions;

import org.code.toboggan.core.extension.APIExtensionManager;
import org.code.toboggan.core.extension.APIFailureEvent;
import java.util.HashMap;
import java.util.Map;

import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.project.IProjectCreateExtension;

import dataMgmt.SessionStorage;
import websocket.IRequestSendErrorHandler;
import websocket.WSManager;
import websocket.models.Project;
import websocket.models.Request;
import websocket.models.requests.ProjectCreateRequest;
import websocket.models.requests.ProjectDeleteRequest;
import websocket.models.requests.ProjectLookupRequest;
import websocket.models.requests.ProjectSubscribeRequest;
import websocket.models.responses.ProjectCreateResponse;
import websocket.models.responses.ProjectLookupResponse;

/**
 * Extends the ProjectCreate API call to create a project on the server.
 * @author fahslaj
 */
public class ProjectCreateExtension implements IProjectCreateExtension {
	
	private SessionStorage storage;
	private WSManager wsMgr;
	private APIExtensionManager extMgr;
	
	public ProjectCreateExtension(SessionStorage storage, WSManager wsMgr, APIExtensionManager extMgr) {
		this.storage = storage;
		this.wsMgr = wsMgr;
		this.extMgr = extMgr;
	}

	@Override
	public void projectCreated(String name) {
		Request createRequest = (new ProjectCreateRequest(name)).getRequest(createResponse -> {
			int createStatus = createResponse.getStatus();
			if (createStatus == 200) {
				long projectId = ((ProjectCreateResponse) createResponse.getData()).getProjectID();
				Request subscribeRequest = (new ProjectSubscribeRequest(projectId)).getRequest(subscribeResponse -> {
					int subscribeStatus = subscribeResponse.getStatus();
					if (subscribeStatus == 200) {
						Long[] ids = {projectId};
						Request lookupRequest = (new ProjectLookupRequest(ids)).getRequest(lookupResponse -> {
							int lookupStatus = lookupResponse.getStatus();
							if (lookupStatus == 200) {
								Project project = ((ProjectLookupResponse) lookupResponse.getData()).getProjects()[0];
								this.storage.setProject(project); // TODO: change to notify of success
							} else {
								handleCreationError(projectId);
							}
						}, getRequestSendHandler(projectId));
						wsMgr.sendAuthenticatedRequest(lookupRequest);
					} else {
						handleCreationError(projectId);
					}
				}, getRequestSendHandler(projectId));
				wsMgr.sendAuthenticatedRequest(subscribeRequest);
			} else {
				handleCreationError(null);
			}
		}, getRequestSendHandler(null));
		wsMgr.sendAuthenticatedRequest(createRequest);
	}
	
	private void handleCreationError(Long id) {
		if (id != null) {
			// delete the project on the server that had already been created
			Request deleteRequest = (new ProjectDeleteRequest(id)).getRequest(null, null); // TODO: panic unsubscribe after unsubscribe is implemented 
			wsMgr.sendAuthenticatedRequest(deleteRequest);
		}
	}
	
	private IRequestSendErrorHandler getRequestSendHandler(Long id) {
		return () -> {
			handleCreationError(id);
		};
	}
}
