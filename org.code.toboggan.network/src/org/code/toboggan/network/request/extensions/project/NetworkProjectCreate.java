package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectCreateExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.project.IProjectCreateResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectCreateRequest;
import clientcore.websocket.models.requests.ProjectLookupRequest;
import clientcore.websocket.models.requests.ProjectSubscribeRequest;
import clientcore.websocket.models.responses.ProjectCreateResponse;
import clientcore.websocket.models.responses.ProjectLookupResponse;

/**
 * Extends the ProjectCreate API call to create a project on the server.
 * 
 * @author fahslaj
 */
public class NetworkProjectCreate extends AbstractNetworkExtension implements IProjectCreateExtension {
	private Logger logger = LogManager.getLogger(NetworkProjectCreate.class);

	public NetworkProjectCreate() {
		super();
	}

	@Override
	public void projectCreated(String name) {
		extMgr = NetworkExtensionManager.getInstance();
		// Make project create request
		Request createRequest = (new ProjectCreateRequest(name)).getRequest(createResponse -> {
			int createStatus = createResponse.getStatus();

			if (createStatus == 200) {
				long projectId = ((ProjectCreateResponse) createResponse.getData()).projectID;
				logger.info("Successfully created project: " + projectId);

				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_CREATE_REQUEST_ID,
						IProjectCreateResponse.class);
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
						Long[] ids = { projectId };
						Request lookupRequest = (new ProjectLookupRequest(ids)).getRequest(lookupResponse -> {
							int lookupStatus = lookupResponse.getStatus();
							if (lookupStatus == 200) {
								logger.info("Successfully fetched project " + projectId + " from server");
								// Trigger extensions
								Project project = ((ProjectLookupResponse) lookupResponse.getData()).projects[0];
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_CREATE_REQUEST_ID,
				IProjectCreateResponse.class);
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_CREATE_REQUEST_ID,
				IProjectCreateResponse.class);
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_CREATE_REQUEST_ID,
				IProjectCreateResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectCreateResponse p = (IProjectCreateResponse) e;
			p.projectFetchFailed(id);
		}
	}

	private IRequestSendErrorHandler getLookupSendHandler(long id) {
		return () -> handleLookupError(id);
	}
}
