package org.code.toboggan.network.request.extensions.project;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectFetchSubscribeAllExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.project.IProjectFetchAndSubscribeAllResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.UserProjectsRequest;
import clientcore.websocket.models.responses.UserProjectsResponse;

public class NetworkProjectFetchAndSubscribeAll extends AbstractNetworkExtension
		implements IProjectFetchSubscribeAllExtension {
	private Logger logger = LogManager.getLogger(NetworkProjectFetchAndSubscribeAll.class);

	public NetworkProjectFetchAndSubscribeAll() {
		super();
	}

	@Override
	public void projectFetchSubscribeAllOccurred(List<Long> subscribedIds) {
		extMgr = NetworkExtensionManager.getInstance();
		Request getProjectsRequest = new UserProjectsRequest().getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Success fetching projects");
				List<Project> projects = Arrays.asList(((UserProjectsResponse) response.getData()).projects);
				Set<ICoreExtension> extensions = extMgr.getExtensions(
						NetworkExtensionIDs.PROJECT_FETCH_SUBSCRIBE_ALL_REQUEST_ID,
						IProjectFetchAndSubscribeAllResponse.class);
				for (ICoreExtension e : extensions) {
					IProjectFetchAndSubscribeAllResponse p = (IProjectFetchAndSubscribeAllResponse) e;
					p.fetchedAll(projects);
				}
				// There is a potential need for a projects.lookup request to
				// get the permission maps of the projects if the server doens't
				// correctly
				// return them from User.Projects. This bug was supposedly
				// fixed, but I'm not sure.
				// List<Long> projectIds =
				// projects.stream().mapToLong(Project::getProjectID).boxed().collect(Collectors.toList());
				// ^^ convert projects to list of ids

				// Convert to a set for quick lookup.
				Set<Long> returnedProjectIDs = new HashSet<>(
						Arrays.asList(projects.stream().map(p -> p.getProjectID()).toArray(Long[]::new)));

				for (long id : subscribedIds) {
					// Only attempt to subscribe if the project ID was returned
					// in the User.Projects request; if it is not in the
					// returned projects,
					// it has been deleted, or we no longer have permissions to
					// it.
					if (returnedProjectIDs.contains(id)) {
						APIFactory.createProjectSubscribe(id).runAsync();
					}
				}
			} else {
				handleFetchError();
			}
		}, getFetchRequestSendHandler());
		wsMgr.sendAuthenticatedRequest(getProjectsRequest);
	}

	private void handleFetchError() {
		logger.error("Failed to fetch projects");
		Set<ICoreExtension> extensions = extMgr.getExtensions(
				NetworkExtensionIDs.PROJECT_FETCH_SUBSCRIBE_ALL_REQUEST_ID, IProjectFetchAndSubscribeAllResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectFetchAndSubscribeAllResponse p = (IProjectFetchAndSubscribeAllResponse) e;
			p.fetchAllFailed();
		}
	}

	private IRequestSendErrorHandler getFetchRequestSendHandler() {
		return () -> handleFetchError();
	}
}
