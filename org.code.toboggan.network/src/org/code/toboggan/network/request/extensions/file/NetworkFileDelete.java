package org.code.toboggan.network.request.extensions.file;

import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFileDeleteExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.file.IFileDeleteResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.FileDeleteRequest;

public class NetworkFileDelete implements IFileDeleteExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkFileDelete.class);
	
	public NetworkFileDelete() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void fileDeleted(long fileID) {
		Request deleteFileReq = new FileDeleteRequest(fileID).getRequest(response -> {
            int status = response.getStatus();
            if (status == 200) {
            	Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_DELETE_ID, IFileDeleteResponse.class);
                for (ICoreExtension e : extensions) {
					IFileDeleteResponse p = (IFileDeleteResponse) e;
					p.fileDeleted(fileID);
				}
            } else {
            	handleDeleteError(fileID);
            }
        }, getDeleteSendHandler(fileID));
        this.wsMgr.sendAuthenticatedRequest(deleteFileReq);
	}
	
	private void handleDeleteError(long fileID) {
		logger.error(String.format("Failed to delete file \"%d\" on the server.", fileID));
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_DELETE_ID, IFileDeleteResponse.class);
        for (ICoreExtension e : extensions) {
			IFileDeleteResponse p = (IFileDeleteResponse) e;
			p.fileDeleteFailed(fileID);
		}
	}
	
	private IRequestSendErrorHandler getDeleteSendHandler(long fileID) {
		return () -> handleDeleteError(fileID);
	}

}
