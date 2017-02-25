package org.code.toboggan.modelmgr.extensions.project;

import org.code.toboggan.network.request.extensionpoints.project.IProjectGetPermissionConstantsResponse;

import com.google.common.collect.BiMap;

public class ModelMgrProjectGetPermissionConstants extends AbstractProjectModelMgrHandler implements IProjectGetPermissionConstantsResponse {

	@Override
	public void getPermissionConstants(BiMap<String, Byte> permConstants) {
		ss.setPermissionConstants(permConstants);
	}

	@Override
	public void getPermissionConstantsFailed() {
		// Do nothing
	}
	
}
