package org.code.toboggan.network.request.extensions.project;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectSubscribeExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.project.IProjectSubscribeResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectGetFilesRequest;
import clientcore.websocket.models.requests.ProjectSubscribeRequest;
import clientcore.websocket.models.responses.ProjectGetFilesResponse;

public class NetworkProjectSubscribe extends AbstractNetworkExtension implements IProjectSubscribeExtension {
	private Logger logger = LogManager.getLogger(NetworkProjectSubscribe.class);

	public NetworkProjectSubscribe() {
		super();
	}

	@Override
	public void subscribed(long projectID) {
		extMgr = NetworkExtensionManager.getInstance();
		Request subscribeRequest = (new ProjectSubscribeRequest(projectID)).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Project-Subscribe: Subscribed to project: " + projectID);
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_SUBSCRIBE_REQUEST_ID,
						IProjectSubscribeResponse.class);

				Request getFilesRequest = (new ProjectGetFilesRequest(projectID)).getRequest(getFilesResponse -> {
					if (getFilesResponse.getStatus() == 200) {
						logger.info("Project-Subscribe: got files for project " + projectID);
						ProjectGetFilesResponse gfResponse = (ProjectGetFilesResponse) getFilesResponse.getData();
						List<File> fileList = Arrays.asList(gfResponse.files);

						for (ICoreExtension e : extensions) {
							IProjectSubscribeResponse p = (IProjectSubscribeResponse) e;
							p.subscribed(projectID, fileList);
						}
					} else {
						for (ICoreExtension e : extensions) {
							IProjectSubscribeResponse p = (IProjectSubscribeResponse) e;
							p.subscribeFailed(projectID);
						}
					}
				}, getGetFilesSendHandler(projectID));
				wsMgr.sendAuthenticatedRequest(getFilesRequest);

			} else {
				handleSubscribeError(projectID);
			}
		}, getRequestSendHandler(projectID));
		wsMgr.sendAuthenticatedRequest(subscribeRequest);
	}

	private void handleSubscribeError(long projectID) {
		logger.error("Failed to subscribe to project: " + projectID);
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_SUBSCRIBE_REQUEST_ID,
				IProjectSubscribeResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectSubscribeResponse p = (IProjectSubscribeResponse) e;
			p.subscribeFailed(projectID);
		}
	}

	private IRequestSendErrorHandler getRequestSendHandler(long projectID) {
		return () -> handleSubscribeError(projectID);
	}

	private void handleGetFilesError(long projectID) {
		logger.info("Project-Subscribe: failed to get files for project " + projectID);
	}

	private IRequestSendErrorHandler getGetFilesSendHandler(long projectID) {
		return () -> handleGetFilesError(projectID);
	}
}
