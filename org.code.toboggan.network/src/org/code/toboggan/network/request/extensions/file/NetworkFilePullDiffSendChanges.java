package org.code.toboggan.network.request.extensions.file;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.file.IFilePullDiffSendChangesExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.file.IFilePullDiffSendChangesResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.FilePullRequest;
import clientcore.websocket.models.responses.FilePullResponse;

public class NetworkFilePullDiffSendChanges implements IFilePullDiffSendChangesExtension {
	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkFilePullDiffSendChanges.class);
	
	public NetworkFilePullDiffSendChanges() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}
	
	@Override
	public void filePulled(long fileID) {
		Request req = (new FilePullRequest(fileID)).getRequest(response -> {
			if (response.getStatus() == 200) {
				byte[] fileBytes = ((FilePullResponse) response.getData()).getFileBytes();
				Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_PULL_DIFF_SEND_CHANGES_ID);
		        for (ICoreExtension e : extensions) {
					IFilePullDiffSendChangesResponse p = (IFilePullDiffSendChangesResponse) e;
					p.filePulled(fileBytes);
				}
			} else {
				handlePullError(fileID);
			}
			
		}, getPullSendHandler(fileID));
		
		wsMgr.sendAuthenticatedRequest(req);
	}

	private void handlePullError(long fileID) {
		logger.error(String.format("Failed to pull file on server: %d", fileID));
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.FILE_PULL_DIFF_SEND_CHANGES_ID);
        for (ICoreExtension e : extensions) {
			IFilePullDiffSendChangesResponse p = (IFilePullDiffSendChangesResponse) e;
			p.filePullFailed(fileID);
		}
	}
	
	private IRequestSendErrorHandler getPullSendHandler(long fileID) {
		return () -> handlePullError(fileID);
	}
}
