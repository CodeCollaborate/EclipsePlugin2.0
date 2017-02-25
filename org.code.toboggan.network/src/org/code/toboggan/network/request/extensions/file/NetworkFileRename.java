package org.code.toboggan.network.request.extensions.file;

import java.nio.file.Path;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFileRenameExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.file.IFileRenameResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.FileRenameRequest;

public class NetworkFileRename implements IFileRenameExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkFileRename.class);
	
	public NetworkFileRename() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void fileRenamed(long fileID, Path oldAbsolutePath, Path newAbsolutePath, String newName) {
		Request renameFileReq = new FileRenameRequest(fileID, newName).getRequest(response -> {
            int status = response.getStatus();
            if (status == 200) {
            	Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_RENAME_ID, IFileRenameResponse.class);
                for (ICoreExtension e : extensions) {
        			IFileRenameResponse p = (IFileRenameResponse) e;
        			p.fileRenamed(fileID, newAbsolutePath, newName);
        		}
            } else {
            	handleRenameError(fileID, oldAbsolutePath, newAbsolutePath, newName);
            }
        }, getRenameSendHandler(fileID, oldAbsolutePath, newAbsolutePath, newName));
        this.wsMgr.sendAuthenticatedRequest(renameFileReq);
	}
	
	private void handleRenameError(long fileID, Path oldAbsolutePath, Path newAbsolutePath, String newName) {
		logger.error(String.format("Failed to rename file on server to %s", newName));
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_RENAME_ID, IFileRenameResponse.class);
        for (ICoreExtension e : extensions) {
			IFileRenameResponse p = (IFileRenameResponse) e;
			p.fileRenameFailed(fileID, oldAbsolutePath, newAbsolutePath, newName);
		}
	}
	
	private IRequestSendErrorHandler getRenameSendHandler(long fileID, Path oldAbsolutePath, Path newAbsolutePath, String newName) {
		return () -> handleRenameError(fileID, oldAbsolutePath, newAbsolutePath, newName);
	}

}
