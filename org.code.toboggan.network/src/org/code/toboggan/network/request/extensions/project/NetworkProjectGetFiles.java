package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectGetFilesExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.project.IProjectGetFilesResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectGetFilesRequest;
import clientcore.websocket.models.responses.ProjectGetFilesResponse;

public class NetworkProjectGetFiles extends AbstractNetworkExtension implements IProjectGetFilesExtension {
	private Logger logger = LogManager.getLogger(NetworkProjectGetFiles.class);
	
	public NetworkProjectGetFiles() {
		super();
	}
	
	@Override
	public void getFiles(long projectID) {
		extMgr = NetworkExtensionManager.getInstance();
		Request requestForFiles = (new ProjectGetFilesRequest(projectID)).getRequest(response -> {
            int status = response.getStatus();
            if (status == 200) {
            	logger.info("Successfully fetched files for project " + projectID);
                ProjectGetFilesResponse r = (ProjectGetFilesResponse) response.getData();
                Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_GET_FILES_REQUEST_ID, IProjectGetFilesResponse.class);
        		for (ICoreExtension e : extensions) {
        			IProjectGetFilesResponse p = (IProjectGetFilesResponse) e;
        			p.projectGetFiles(projectID, r.files);
        		}
            } else {
            }
		}, getRequestSendHandler(projectID));
		
		wsMgr.sendAuthenticatedRequest(requestForFiles);
	}
	
	private void handleGetFilesError(long projectID) {
		logger.error("Failed to get files project from server");
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.PROJECT_GET_FILES_REQUEST_ID, IProjectGetFilesResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectGetFilesResponse p = (IProjectGetFilesResponse) e;
			p.projectGetFilesFailed(projectID);
		}
	}

	private IRequestSendErrorHandler getRequestSendHandler(long projectID) {
		return () -> {
			handleGetFilesError(projectID);
		};
	}
	
}
