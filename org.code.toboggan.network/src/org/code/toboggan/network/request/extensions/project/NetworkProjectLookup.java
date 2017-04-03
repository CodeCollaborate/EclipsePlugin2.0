package org.code.toboggan.network.request.extensions.project;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectLookupExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.project.IProjectLookupResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectLookupRequest;
import clientcore.websocket.models.responses.ProjectLookupResponse;

public class NetworkProjectLookup extends AbstractNetworkExtension implements IProjectLookupExtension {
	private Logger logger = LogManager.getLogger(NetworkProjectLookup.class);

	public NetworkProjectLookup() {
		super();
	}

	@Override
	public void lookupProject(List<Long> projectIDs) {
		extMgr = NetworkExtensionManager.getInstance();
		Request lookupRequest = (new ProjectLookupRequest(projectIDs)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Performed project lookup for projects: "
						+ projectIDs.stream().map(Object::toString).collect(Collectors.joining(", ")));
				List<Project> projects = Arrays.asList(((ProjectLookupResponse) response.getData()).projects);
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_LOOKUP_REQUEST_ID,
						IProjectLookupResponse.class);
				for (ICoreExtension e : extensions) {
					IProjectLookupResponse p = (IProjectLookupResponse) e;
					p.projectFound(projects);
				}
			} else {
				handleProjectLookupError(projectIDs);
			}
		}, getRequestSendHandler(projectIDs));
		this.wsMgr.sendAuthenticatedRequest(lookupRequest);
	}

	private void handleProjectLookupError(List<Long> projectIDs) {
		logger.error("Failed to lookup projects: "
				+ projectIDs.stream().map(Object::toString).collect(Collectors.joining(", ")));
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_LOOKUP_REQUEST_ID,
				IProjectLookupResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectLookupResponse p = (IProjectLookupResponse) e;
			p.projectLookupFailed(projectIDs);
		}
	}

	private IRequestSendErrorHandler getRequestSendHandler(List<Long> projectIDs) {
		return () -> handleProjectLookupError(projectIDs);
	}
}
