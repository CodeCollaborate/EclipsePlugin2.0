package org.code.toboggan.network.request.extensions.file;

import java.nio.file.Path;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFileMoveExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.file.IFileMoveResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.utils.NetworkUtils;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.FileMoveRequest;

public class NetworkFileMove implements IFileMoveExtension {

	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private SessionStorage ss;
	private Logger logger = LogManager.getLogger(NetworkFileMove.class);
	
	public NetworkFileMove() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
		this.ss = CoreActivator.getSessionStorage();
	}
	
	@Override
	public void fileMoved(long fileID, Path oldAbsolutePath, Path newFileLocation) {
		File file = ss.getFile(fileID);
		long projectID = file.getProjectID();
		Path projectLocation = ss.getProjectLocation(projectID);
		String stringProjectRelative = NetworkUtils.toStringRelativePath(projectLocation, newFileLocation);
		
		Request moveFileReq = new FileMoveRequest(fileID, stringProjectRelative).getRequest(response -> {
            int status = response.getStatus();
            if (status == 200) {
            	Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_MOVE_ID, IFileMoveResponse.class);
                for (ICoreExtension e : extensions) {
        			IFileMoveResponse p = (IFileMoveResponse) e;
        			p.fileMoved(fileID, newFileLocation);
        		}
            } else {
            	handleMoveError(fileID, oldAbsolutePath, newFileLocation);
            }
        }, getMoveSendHandler(fileID, oldAbsolutePath, newFileLocation));
        this.wsMgr.sendAuthenticatedRequest(moveFileReq);
	}
	
	private void handleMoveError(long fileID, Path oldAbsolutePath, Path newAbsolutePath) {
		logger.error(String.format("Failed to move file on server: %d", fileID));
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_MOVE_ID, IFileMoveResponse.class);
        for (ICoreExtension e : extensions) {
			IFileMoveResponse p = (IFileMoveResponse) e;
			p.fileMoveFailed(fileID, oldAbsolutePath, newAbsolutePath);
		}
	}
	
	private IRequestSendErrorHandler getMoveSendHandler(long fileID, Path oldAbsolutePath, Path newAbsolutePath) {
		return () -> handleMoveError(fileID, oldAbsolutePath, newAbsolutePath);
	}

}
