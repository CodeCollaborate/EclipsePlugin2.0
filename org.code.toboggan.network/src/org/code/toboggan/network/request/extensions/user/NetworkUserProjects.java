package org.code.toboggan.network.request.extensions.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.user.IUserProjectsExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.user.IUserProjectsResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.UserProjectsRequest;
import clientcore.websocket.models.responses.UserProjectsResponse;

public class NetworkUserProjects implements IUserProjectsExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkUserProjects.class);
	
	public NetworkUserProjects() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void userProjects() {
		Request projectsRequest = (new UserProjectsRequest()).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Successfully fetched projects");
				List<Project> projects = Collections.unmodifiableList(
						Arrays.asList(
								((UserProjectsResponse) response.getData()).getProjects()
								)
						);
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.USER_PROJECTS_ID, IUserProjectsResponse.class);
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.USER_PROJECTS_ID, IUserProjectsResponse.class);
		for (ICoreExtension e : extensions) {
			IUserProjectsResponse p = (IUserProjectsResponse) e;
			p.userProjectsFailed();
		}
	}
	
	private IRequestSendErrorHandler getSendRequestHandler() {
		return () -> handleFetchError();
	}
}
