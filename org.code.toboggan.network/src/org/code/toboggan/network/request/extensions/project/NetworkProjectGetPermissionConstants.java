package org.code.toboggan.network.request.extensions.project;

import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.core.extension.project.IProjectGetPermissionConstantsExtension;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensionpoints.project.IProjectGetPermissionConstantsResponse;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import com.google.common.collect.BiMap;

import clientcore.websocket.IRequestSendErrorHandler;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.ProjectGetPermissionConstantsRequest;
import clientcore.websocket.models.responses.ProjectGetPermissionConstantsResponse;

public class NetworkProjectGetPermissionConstants implements IProjectGetPermissionConstantsExtension {
	
	private WSManager wsMgr;
	private AbstractExtensionManager extMgr;
	private Logger logger = LogManager.getLogger(NetworkProjectGetPermissionConstants.class);
	
	public NetworkProjectGetPermissionConstants() {
		this.wsMgr = WSService.getWSManager();
		this.extMgr = NetworkExtensionManager.getInstance();
	}

	@Override
	public void getPermissionConstants() {
		Request getPermConstants = new ProjectGetPermissionConstantsRequest().getRequest(response -> {
            int status = response.getStatus();
            if (status == 200) {
            	logger.info("Successfully fetched permission constants");
                BiMap<String, Byte> permConstants =
                        (((ProjectGetPermissionConstantsResponse) response.getData()).getConstants());
                Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_GET_PERMISSIONS_CONST_ID, IProjectGetPermissionConstantsResponse.class);
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
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_GET_PERMISSIONS_CONST_ID, IProjectGetPermissionConstantsResponse.class);
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
