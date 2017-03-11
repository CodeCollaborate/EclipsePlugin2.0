package org.code.toboggan.network.request.extensions.file;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFileChangeExtension;
import org.code.toboggan.network.NetworkActivator;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.file.IFileChangeResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.dataMgmt.SessionStorage;
import clientcore.patching.Patch;
import clientcore.patching.PatchManager;
import clientcore.websocket.models.responses.FileChangeResponse;

public class NetworkFileChange extends AbstractNetworkExtension implements IFileChangeExtension {
	private AbstractExtensionManager extMgr;
	private SessionStorage ss;
	private PatchManager pm;
	private Logger logger = LogManager.getLogger(NetworkFileChange.class);
	
	public NetworkFileChange() {
		super();
		this.ss = CoreActivator.getSessionStorage();
		this.pm = NetworkActivator.getPatchManager();
	}

	@Override
	public void fileChanged(long fileID, Patch[] patches) {
		extMgr = NetworkExtensionManager.getInstance();
		pm.sendPatch(fileID, patches, response -> {
            long version = ((FileChangeResponse) response.getData()).fileVersion;
            
            Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_CHANGE_REQUEST_ID, IFileChangeResponse.class);
            for (ICoreExtension e : extensions) {
            	IFileChangeResponse p = (IFileChangeResponse) e;
				p.fileChanged(fileID, version);
			}
        }, null);
	}

}
