package org.code.toboggan.network.request.extensions.project;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectFetchSubscribeAllExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectFetchAndSubscribeAllResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.requests.ProjectSubscribeRequest;
import clientcore.websocket.models.requests.UserProjectsRequest;
import clientcore.websocket.models.responses.UserProjectsResponse;

public class NetworkProjectFetchAndSubscribeAll implements IProjectFetchSubscribeAllExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectFetchAndSubscribeAll.class);
	
	public NetworkProjectFetchAndSubscribeAll() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void projectFetchSubscribeAllOccurred(List<Long> subscribedIds) {
		Request getProjectsRequest = new UserProjectsRequest().getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Success fetching projects");
				List<Project> projects = Arrays.asList(((UserProjectsResponse) response.getData()).getProjects());
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_FETCH_SUBSCRIBE_ALL);
				for (ICoreExtension e : extensions) {
					IProjectFetchAndSubscribeAllResponse p = (IProjectFetchAndSubscribeAllResponse) e;
					p.fetchedAll(projects);
				}
				// There is a potential need for a projects.lookup request to get the permission maps of the projects if the server doens't correctly
				// return them from User.Projects. This bug was supposedly fixed, but I'm not sure.
				// List<Long> projectIds = projects.stream().mapToLong(Project::getProjectID).boxed().collect(Collectors.toList());
				// ^^ convert projects to list of ids
				for (long id : subscribedIds) {
					new Thread(APIFactory.createProjectSubscribe(id)).start();
				}
			} else {
				handleFetchError();
			}
		}, getFetchRequestSendHandler());
		wsMgr.sendAuthenticatedRequest(getProjectsRequest);
	}
	
	private void handleFetchError() {
		logger.error("Failed to fetch projects");
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_FETCH_SUBSCRIBE_ALL);
		for (ICoreExtension e : extensions) {
			IProjectFetchAndSubscribeAllResponse p = (IProjectFetchAndSubscribeAllResponse) e;
			p.fetchAllFailed();
		}
	}

	private IRequestSendErrorHandler getFetchRequestSendHandler() {
		return () -> handleFetchError();
	}

}
