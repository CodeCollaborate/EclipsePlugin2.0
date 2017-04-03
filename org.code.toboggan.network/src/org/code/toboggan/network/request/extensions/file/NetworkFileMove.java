package org.code.toboggan.network.request.extensions.file;

import java.nio.file.Path;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFileMoveExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.file.IFileMoveResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.utils.NetworkUtils;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.FileMoveRequest;

public class NetworkFileMove extends AbstractNetworkExtension implements IFileMoveExtension {

	private SessionStorage ss;
	private Logger logger = LogManager.getLogger(NetworkFileMove.class);

	public NetworkFileMove() {
		super();
		this.ss = CoreActivator.getSessionStorage();
	}

	@Override
	public void fileMoved(long fileID, Path oldAbsolutePath, Path newFileLocation) {
		extMgr = NetworkExtensionManager.getInstance();

		File file = ss.getFile(fileID);
		long projectID = file.getProjectID();
		Path projectLocation = ss.getProjectLocation(projectID);

		if (!newFileLocation.isAbsolute()) {
			throw new IllegalArgumentException("newFileLocation was not absolute: " + newFileLocation.toString());
		}
		String stringProjectRelative = NetworkUtils.toStringRelativePath(projectLocation, newFileLocation);

		Request moveFileReq = new FileMoveRequest(fileID, stringProjectRelative).getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_MOVE_REQUEST_ID,
						IFileMoveResponse.class);
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
		logger.error(String.format("Failed to move file on server: [%d]", fileID));
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_MOVE_REQUEST_ID,
				IFileMoveResponse.class);
		for (ICoreExtension e : extensions) {
			IFileMoveResponse p = (IFileMoveResponse) e;
			p.fileMoveFailed(fileID, oldAbsolutePath, newAbsolutePath);
		}
	}

	private IRequestSendErrorHandler getMoveSendHandler(long fileID, Path oldAbsolutePath, Path newAbsolutePath) {
		return () -> handleMoveError(fileID, oldAbsolutePath, newAbsolutePath);
	}

}
