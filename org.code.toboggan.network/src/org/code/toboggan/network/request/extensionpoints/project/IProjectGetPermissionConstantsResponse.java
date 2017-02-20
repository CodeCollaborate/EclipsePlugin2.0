package org.code.toboggan.network.request.extensionpoints.project;

import com.google.common.collect.BiMap;

public interface IProjectGetPermissionConstantsResponse {
	public void getPermissionConstants(BiMap<String, Byte> permConstants);
	public void getPermissionConstantsFailed();
}
