package org.code.toboggan.network.request.extensions.file;

import java.nio.file.Path;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFileMoveExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.file.IFileMoveResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

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
		this.ss = null; // TODO: get from core
	}
	
	@Override
	public void fileMoved(long fileID, Path newAbsolutePath) {
		File file = ss.getFile(fileID);
		long projectID = file.getProjectID();
		Path projectLocation = ss.getProjectLocation(projectID);
		Path projectRelativePath = projectLocation.relativize(newAbsolutePath);
		String stringProjectRelative = FilenameUtils.getPath(projectRelativePath.toString());
		logger.debug("Project relativized path: " + stringProjectRelative);
		
		Request moveFileReq = new FileMoveRequest(fileID, stringProjectRelative).getRequest(response -> {
            int status = response.getStatus();
            if (status == 200) {
            	Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_MOVE_ID);
                for (ICoreExtension e : extensions) {
        			IFileMoveResponse p = (IFileMoveResponse) e;
        			p.fileMoved(fileID, newAbsolutePath);
        		}
            } else {
            	handleMoveError(fileID, newAbsolutePath);
            }
        }, getMoveSendHandler(fileID, newAbsolutePath));
        this.wsMgr.sendAuthenticatedRequest(moveFileReq);
	}
	
	private void handleMoveError(long fileID, Path newAbsolutePath) {
		logger.error(String.format("Failed to move file on server: %d", fileID));
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_MOVE_ID);
        for (ICoreExtension e : extensions) {
			IFileMoveResponse p = (IFileMoveResponse) e;
			p.fileMoveFailed(fileID, newAbsolutePath);
		}
	}
	
	private IRequestSendErrorHandler getMoveSendHandler(long fileID, Path newAbsolutePath) {
		return () -> handleMoveError(fileID, newAbsolutePath);
	}

}
