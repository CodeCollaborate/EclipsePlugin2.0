package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.project.IProjectGetPermissionConstantsExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.project.IProjectGetPermissionConstantsResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectGetPermissionConstantsRequest;
import clientcore.websocket.models.responses.ProjectGetPermissionConstantsResponse;

public class NetworkProjectGetPermissionConstants extends AbstractNetworkExtension
		implements IProjectGetPermissionConstantsExtension {
	private Logger logger = LogManager.getLogger(NetworkProjectGetPermissionConstants.class);

	public NetworkProjectGetPermissionConstants() {
		super();
	}

	@Override
	public void getPermissionConstants() {
		extMgr = NetworkExtensionManager.getInstance();
		Request getPermConstants = new ProjectGetPermissionConstantsRequest().getRequest(response -> {
			int status = response.getStatus();
			if (status == 200) {
				logger.info("Successfully fetched permission constants");
				BiMap<String, Integer> permConstants = HashBiMap
						.create((((ProjectGetPermissionConstantsResponse) response.getData()).constants));
				Set<ICoreExtension> extensions = extMgr.getExtensions(
						NetworkExtensionIDs.PROJECT_GET_PERMISSIONS_CONST_REQUEST_ID,
						IProjectGetPermissionConstantsResponse.class);
				for (ICoreExtension e : extensions) {
					IProjectGetPermissionConstantsResponse p = (IProjectGetPermissionConstantsResponse) e;
					p.getPermissionConstants(permConstants);
				}
			} else {
				handleGetPermissionConstantsError();
			}
		}, getRequestSendHandler());
		this.wsMgr.sendAuthenticatedRequest(getPermConstants);
	}

	private void handleGetPermissionConstantsError() {
		logger.error("Failed to get permission constants from server");
		Set<ICoreExtension> extensions = extMgr.getExtensions(
				NetworkExtensionIDs.PROJECT_GET_PERMISSIONS_CONST_REQUEST_ID,
				IProjectGetPermissionConstantsResponse.class);
		for (ICoreExtension e : extensions) {
			IProjectGetPermissionConstantsResponse p = (IProjectGetPermissionConstantsResponse) e;
			p.getPermissionConstantsFailed();
		}
	}

	private IRequestSendErrorHandler getRequestSendHandler() {
		return () -> {
			handleGetPermissionConstantsError();
		};
	}

}
