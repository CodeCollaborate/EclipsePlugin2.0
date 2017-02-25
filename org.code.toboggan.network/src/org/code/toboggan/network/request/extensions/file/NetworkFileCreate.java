package org.code.toboggan.network.request.extensions.file;

import java.nio.file.Path;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFileCreateExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.file.IFileCreateResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.utils.NetworkUtils;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.FileCreateRequest;
import clientcore.websocket.models.responses.FileCreateResponse;

public class NetworkFileCreate implements IFileCreateExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private SessionStorage ss;
	private Logger logger = LogManager.getLogger(NetworkFileCreate.class);
	
	public NetworkFileCreate() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
		this.ss = CoreActivator.getSessionStorage();
	}
	
	@Override
	public void fileCreated(String name, Path fileLocation, long projectID, byte[] fileBytes) {
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
                long fileID = ((FileCreateResponse) response.getData()).getFileID();
                
                Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_CREATE_ID, IFileCreateResponse.class);
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
		logger.error(String.format("Failed to create file \"%s\" on the server.", name));
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_CREATE_ID, IFileCreateResponse.class);
        for (ICoreExtension e : extensions) {
			IFileCreateResponse p = (IFileCreateResponse) e;
			p.fileCreateFailed(name, fileLocation, projectID, fileBytes);
		}
	}
	
	private IRequestSendErrorHandler getCreateSendHandler(String name, Path fileLocation, long projectID, byte[] fileBytes) {
		return () -> handleCreateError(name, fileLocation, projectID, fileBytes);
	}
}
