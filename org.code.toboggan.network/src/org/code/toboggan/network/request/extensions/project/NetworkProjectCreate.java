package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectCreateExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectCreateResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectCreateRequest;
import clientcore.websocket.models.requests.ProjectDeleteRequest;
import clientcore.websocket.models.requests.ProjectLookupRequest;
import clientcore.websocket.models.requests.ProjectSubscribeRequest;
import clientcore.websocket.models.responses.ProjectCreateResponse;
import clientcore.websocket.models.responses.ProjectLookupResponse;

/**
 * Extends the ProjectCreate API call to create a project on the server.
 * @author fahslaj
 */
public class NetworkProjectCreate implements IProjectCreateExtension {
	
	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectCreate.class);
	
	public NetworkProjectCreate() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}

	@Override
	public void projectCreated(String name) {
		// Make project create request
		Request createRequest = (new ProjectCreateRequest(name)).getRequest(createResponse -> {
			int createStatus = createResponse.getStatus();
			
			if (createStatus == 200) {
				long projectId = ((ProjectCreateResponse) createResponse.getData()).getProjectID();
				
				// Make subscribe request
				Request subscribeRequest = (new ProjectSubscribeRequest(projectId)).getRequest(subscribeResponse -> {
					int subscribeStatus = subscribeResponse.getStatus();
					
					if (subscribeStatus == 200) {
						// Make lookup request
						Long[] ids = {projectId};
						Request lookupRequest = (new ProjectLookupRequest(ids)).getRequest(lookupResponse -> {
							int lookupStatus = lookupResponse.getStatus();
							if (lookupStatus == 200) {
								
								// Trigger extensions 
								Project project = ((ProjectLookupResponse) lookupResponse.getData()).getProjects()[0];
								Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_CREATE_ID);
								for (ICoreExtension e : extensions) {
									IProjectCreateResponse p = (IProjectCreateResponse) e;
									p.projectCreated(project);
								}
								
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
				handleCreationError(name);
			}
		}, getRequestSendHandler(name));
		
		wsMgr.sendAuthenticatedRequest(createRequest);
	}
	
	private void handleCreationError(Long id) {
		logger.error("Created project but failed to subscribe or lookup project.");
		if (id != null) {
			// delete the project on the server that had already been created
			Request deleteRequest = (new ProjectDeleteRequest(id)).getRequest(null, null);
			wsMgr.sendAuthenticatedRequest(deleteRequest);
		}
	}
	
	private void handleCreationError(String projectName) {
		logger.error(String.format("Failed to create project \"%s\"", projectName));
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_CREATE_ID);
		for (ICoreExtension e : extensions) {
			IProjectCreateResponse p = (IProjectCreateResponse) e;
			p.projectCreationFailed(projectName);
		}
	}
	
	private IRequestSendErrorHandler getRequestSendHandler(Long id) {
		return () -> {
			handleCreationError(id);
		};
	}
	
	private IRequestSendErrorHandler getRequestSendHandler(String projectName) {
		return () -> {
			handleCreationError(projectName);
		};
	}
}
