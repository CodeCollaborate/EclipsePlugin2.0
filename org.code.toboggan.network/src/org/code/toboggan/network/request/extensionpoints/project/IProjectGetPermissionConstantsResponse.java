package org.code.toboggan.network.request.extensionpoints.project;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

import com.google.common.collect.BiMap;

public interface IProjectGetPermissionConstantsResponse extends ICoreExtension {
	public void getPermissionConstants(BiMap<String, Byte> permConstants);
	public void getPermissionConstantsFailed();
}
