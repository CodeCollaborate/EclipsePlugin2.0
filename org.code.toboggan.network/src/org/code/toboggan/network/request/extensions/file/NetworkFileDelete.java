package org.code.toboggan.network.request.extensions.file;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFileDeleteExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.file.IFileDeleteResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.FileDeleteRequest;

public class NetworkFileDelete extends AbstractNetworkExtension implements IFileDeleteExtension {
	private Logger logger = LogManager.getLogger(NetworkFileDelete.class);
	
	public NetworkFileDelete() {
		super();
	}
	
	@Override
	public void fileDeleted(long fileID) {
		extMgr = NetworkExtensionManager.getInstance();
		Request deleteFileReq = new FileDeleteRequest(fileID).getRequest(response -> {
            int status = response.getStatus();
            if (status == 200) {
            	Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_DELETE_REQUEST_ID, IFileDeleteResponse.class);
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
		NetworkExtensionManager extMgr = NetworkExtensionManager.getInstance();
		logger.error(String.format("Failed to delete file \"%d\" on the server.", fileID));
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_DELETE_REQUEST_ID, IFileDeleteResponse.class);
        for (ICoreExtension e : extensions) {
			IFileDeleteResponse p = (IFileDeleteResponse) e;
			p.fileDeleteFailed(fileID);
		}
	}
	
	private IRequestSendErrorHandler getDeleteSendHandler(long fileID) {
		return () -> handleDeleteError(fileID);
	}

}
