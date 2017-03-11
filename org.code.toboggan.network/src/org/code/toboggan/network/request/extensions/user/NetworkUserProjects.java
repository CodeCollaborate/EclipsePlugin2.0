package org.code.toboggan.network.request.extensions.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.user.IUserProjectsExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.user.IUserProjectsResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.UserProjectsRequest;
import clientcore.websocket.models.responses.UserProjectsResponse;

public class NetworkUserProjects extends AbstractNetworkExtension implements IUserProjectsExtension {
	private Logger logger = LogManager.getLogger(NetworkUserProjects.class);
	
	public NetworkUserProjects() {
		super();
	}
	
	@Override
	public void userProjects() {
		extMgr = NetworkExtensionManager.getInstance();
		Request projectsRequest = (new UserProjectsRequest()).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Successfully fetched projects");
				List<Project> projects = Collections.unmodifiableList(
						Arrays.asList(((UserProjectsResponse) response.getData()).projects)
						);
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.USER_PROJECTS_REQUEST_ID, IUserProjectsResponse.class);
				for (ICoreExtension e : extensions) {
					IUserProjectsResponse p = (IUserProjectsResponse) e;
					p.projectsRetrieved(projects);
				}
			} else {
				handleFetchError();
			}
		}, getSendRequestHandler());
		wsMgr.sendAuthenticatedRequest(projectsRequest);
	}
	
	private void handleFetchError() {
		logger.error("Error fetching projects for user");
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.USER_PROJECTS_REQUEST_ID, IUserProjectsResponse.class);
		for (ICoreExtension e : extensions) {
			IUserProjectsResponse p = (IUserProjectsResponse) e;
			p.userProjectsFailed();
		}
	}
	
	private IRequestSendErrorHandler getSendRequestHandler() {
		return () -> handleFetchError();
	}
}
