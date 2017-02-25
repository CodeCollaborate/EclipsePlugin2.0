package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectGetFilesExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectGetFilesResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectGetFilesRequest;
import clientcore.websocket.models.responses.ProjectGetFilesResponse;

public class NetworkProjectGetFiles implements IProjectGetFilesExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectGetFiles.class);
	
	public NetworkProjectGetFiles() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void getFiles(long projectID) {
		Request requestForFiles = (new ProjectGetFilesRequest(projectID)).getRequest(response -> {
            int status = response.getStatus();
            if (status == 200) {
            	logger.info("Successfully fetched files for project " + projectID);
                ProjectGetFilesResponse r = (ProjectGetFilesResponse) response.getData();
                Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_GET_FILES_ID);
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_GET_FILES_ID);
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
