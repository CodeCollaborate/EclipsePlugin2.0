package org.code.toboggan.network.request.extensions.file;

import java.nio.file.Path;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFileCreateExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.file.IFileCreateResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.utils.NetworkUtils;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.FileCreateRequest;
import clientcore.websocket.models.responses.FileCreateResponse;

public class NetworkFileCreate extends AbstractNetworkExtension implements IFileCreateExtension {
	private SessionStorage ss;
	private Logger logger = LogManager.getLogger(NetworkFileCreate.class);
	
	public NetworkFileCreate() {
		super();
		this.ss = CoreActivator.getSessionStorage();
	}
	
	@Override
	public void fileCreated(String name, Path fileLocation, long projectID, byte[] fileBytes) {
		NetworkExtensionManager extMgr = NetworkExtensionManager.getInstance();
		
		Path projectLocation = ss.getProjectLocation(projectID);
		String projectRelativePath = NetworkUtils.toStringRelativePath(projectLocation, fileLocation);
		
		String contents = new String(fileBytes);
    	if (contents.contains("\r\n")) {
    		contents = contents.replace("\r\n", "\n");
    	}
    	// Make request
        Request createFileReq = new FileCreateRequest(name, projectRelativePath, projectID, fileBytes).getRequest(response -> {
            int status = response.getStatus();
            if (status == 200) {
                long fileID = ((FileCreateResponse) response.getData()).fileID;
                
                Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_CREATE_REQUEST_ID, IFileCreateResponse.class);
                for (ICoreExtension e : extensions) {
					IFileCreateResponse p = (IFileCreateResponse) e;
					p.fileCreated(fileID, name, fileLocation, projectRelativePath, projectID);
				}
                
            } else {
                handleCreateError(name, fileLocation, projectID, fileBytes);
            }
        }, getCreateSendHandler(name, fileLocation, projectID, fileBytes));
        this.wsMgr.sendAuthenticatedRequest(createFileReq);
	}
	
	private void handleCreateError(String name, Path fileLocation, long projectID, byte[] fileBytes) {
		NetworkExtensionManager extMgr = NetworkExtensionManager.getInstance();
		logger.error(String.format("Failed to create file \"%s\" on the server.", name));
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_CREATE_REQUEST_ID, IFileCreateResponse.class);
        for (ICoreExtension e : extensions) {
			IFileCreateResponse p = (IFileCreateResponse) e;
			p.fileCreateFailed(name, fileLocation, projectID, fileBytes);
		}
	}
	
	private IRequestSendErrorHandler getCreateSendHandler(String name, Path fileLocation, long projectID, byte[] fileBytes) {
		return () -> handleCreateError(name, fileLocation, projectID, fileBytes);
	}
}
