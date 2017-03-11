package org.code.toboggan.network.request.extensions.file;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFilePullDiffSendChangesExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.file.IFilePullDiffSendChangesResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.FilePullRequest;
import clientcore.websocket.models.responses.FilePullResponse;

public class NetworkFilePullDiffSendChanges extends AbstractNetworkExtension implements IFilePullDiffSendChangesExtension {
	private Logger logger = LogManager.getLogger(NetworkFilePullDiffSendChanges.class);
	
	public NetworkFilePullDiffSendChanges() {
		super();
	}
	
	@Override
	public void filePulled(long fileID) {
		extMgr = NetworkExtensionManager.getInstance();
		Request req = (new FilePullRequest(fileID)).getRequest(response -> {
			if (response.getStatus() == 200) {
				FilePullResponse fpResponse = (FilePullResponse) response.getData();
				byte[] fileBytes = fpResponse.fileBytes;
				String[] changes = fpResponse.changes;
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_PULL_DIFF_SEND_CHANGES_REQUEST_ID, IFilePullDiffSendChangesResponse.class);
		        for (ICoreExtension e : extensions) {
					IFilePullDiffSendChangesResponse p = (IFilePullDiffSendChangesResponse) e;
					p.filePulled(fileID, fileBytes, changes);
				}
			} else {
				handlePullError(fileID);
			}
			
		}, getPullSendHandler(fileID));
		
		wsMgr.sendAuthenticatedRequest(req);
	}

	private void handlePullError(long fileID) {
		logger.error(String.format("Failed to pull file on server: %d", fileID));
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_PULL_DIFF_SEND_CHANGES_REQUEST_ID, IFilePullDiffSendChangesResponse.class);
        for (ICoreExtension e : extensions) {
			IFilePullDiffSendChangesResponse p = (IFilePullDiffSendChangesResponse) e;
			p.filePullFailed(fileID);
		}
	}
	
	private IRequestSendErrorHandler getPullSendHandler(long fileID) {
		return () -> handlePullError(fileID);
	}
}
