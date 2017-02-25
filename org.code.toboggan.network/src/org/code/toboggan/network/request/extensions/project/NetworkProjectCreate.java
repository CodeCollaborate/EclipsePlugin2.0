package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
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
				logger.info("Successfully created project: " + projectId);
				
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_CREATE_ID, IProjectCreateResponse.class);
				for (ICoreExtension e : extensions) {
					IProjectCreateResponse p = (IProjectCreateResponse) e;
					p.projectCreated(projectId);
				}
				// Make subscribe request
				Request subscribeRequest = (new ProjectSubscribeRequest(projectId)).getRequest(subscribeResponse -> {
					int subscribeStatus = subscribeResponse.getStatus();
					
					if (subscribeStatus == 200) {
						logger.info("Successfullly subscribed to project: " + projectId);
						
						for (ICoreExtension e : extensions) {
							IProjectCreateResponse p = (IProjectCreateResponse) e;
							p.subscribed(projectId);
						}
						// Make lookup request
						Long[] ids = {projectId};
						Request lookupRequest = (new ProjectLookupRequest(ids)).getRequest(lookupResponse -> {
							int lookupStatus = lookupResponse.getStatus();
							if (lookupStatus == 200) {
								logger.info("Successfully fetched project " + projectId + " from server");
								// Trigger extensions 
								Project project = ((ProjectLookupResponse) lookupResponse.getData()).getProjects()[0];
								for (ICoreExtension e : extensions) {
									IProjectCreateResponse p = (IProjectCreateResponse) e;
									p.projectFetched(project);
								}
							} else {
								handleLookupError(projectId);
							}
						}, getLookupSendHandler(projectId));
						wsMgr.sendAuthenticatedRequest(lookupRequest);
					} else {
						handleSubscribeError(projectId);
					}
				}, getSubscribeSendHandler(projectId));
				wsMgr.sendAuthenticatedRequest(subscribeRequest);
			} else {
				handleCreateError(name);
			}
		}, getCreateSendHandler(name));
		wsMgr.sendAuthenticatedRequest(createRequest);
	}
	
	private void handleCreateError(String name) {
		logger.error("Failed to create project: " + name);
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_CREATE_ID, IProjectCreateResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectCreateResponse p = (IProjectCreateResponse) e;
			p.projectCreationFailed(name);
		}
	}
	
	private IRequestSendErrorHandler getCreateSendHandler(String name) {
		return () -> handleCreateError(name);
	}
	
	private void handleSubscribeError(long id) {
		logger.error("Failed to subscribe to project: " + id);
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_CREATE_ID, IProjectCreateResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectCreateResponse p = (IProjectCreateResponse) e;
			p.subscribeFailed(id);
		}
	}
	
	private IRequestSendErrorHandler getSubscribeSendHandler(long projectId) {
		return () -> handleSubscribeError(projectId);
	}
	
	private void handleLookupError(long id) {
		logger.error("Failed to lookup project: " + id);
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_CREATE_ID, IProjectCreateResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectCreateResponse p = (IProjectCreateResponse) e;
			p.projectFetchFailed(id);
		}
	}
	
	private IRequestSendErrorHandler getLookupSendHandler(long id) {
		return () -> handleLookupError(id);
	}
}
