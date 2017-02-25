package org.code.toboggan.network.request.extensions.project;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectLookupExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectLookupResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectLookupRequest;
import clientcore.websocket.models.responses.ProjectLookupResponse;

public class NetworkProjectLookup implements IProjectLookupExtension {
	
	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectLookup.class);
	
	public NetworkProjectLookup() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}

	@Override
	public void lookupProject(List<Long> projectIDs) {
		Request lookupRequest = (new ProjectLookupRequest(projectIDs)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Performed project lookup for projects: " + projectIDs.stream()
																			.map(Object::toString)
																			.collect(Collectors.joining(", ")));
				List<Project> projects = Arrays.asList(((ProjectLookupResponse) response.getData()).getProjects());
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_LOOKUP_ID, IProjectLookupResponse.class);
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
		logger.error("Failed to lookup projects: " + projectIDs.stream()
														.map(Object::toString)
														.collect(Collectors.joining(", ")));
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_LOOKUP_ID, IProjectLookupResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectLookupResponse p = (IProjectLookupResponse) e;
			p.projectLookupFailed(projectIDs);
		}
	}
	
	private IRequestSendErrorHandler getRequestSendHandler(List<Long> projectIDs) {
		return () -> handleProjectLookupError(projectIDs);
	}
}
