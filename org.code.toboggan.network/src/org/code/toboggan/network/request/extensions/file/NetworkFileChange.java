package org.code.toboggan.network.request.extensions.file;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.core.extensionpoints.file.IFileChangeExtension;
import org.code.toboggan.network.NetworkActivator;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.file.IFileChangeResponse;
import org.code.toboggan.network.request.extensions.AbstractNetworkExtension;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.patching.Patch;
import clientcore.patching.PatchManager;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Permission;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.responses.FileChangeResponse;

public class NetworkFileChange extends AbstractNetworkExtension implements IFileChangeExtension {
	private Logger logger = LogManager.getLogger(this.getClass());
	private AbstractExtensionManager extMgr;
	private PatchManager pm;

	public NetworkFileChange() {
		super();
		this.pm = NetworkActivator.getPatchManager();
	}

	@Override
	public void fileChanged(long fileID, Patch[] patches) {
		File file = CoreActivator.getSessionStorage().getFile(fileID);
		if (file == null) {
			logger.error("File was null for NetworkFileChange");
			return;
		}

		Project project = CoreActivator.getSessionStorage().getProject(file.getProjectID());
		if (project == null) {
			logger.error("Project was null for NetworkFileChange");
			return;
		}

		Permission perm = project.getPermissions().get(CoreActivator.getSessionStorage().getUsername());
		if (CoreActivator.getSessionStorage().getPermissionConstants().get("read") == null) {
			logger.error("Read-level permission was null");
			return;
		}
		if (perm == null || perm.getPermissionLevel() == CoreActivator.getSessionStorage().getPermissionConstants()
				.get("read")) {
			// We don't have permission; do not send patch.
			logger.info("Permission for current user does not exist, or is read-only; saving locally");
			pm.savePatch(fileID, patches);
			return;
		}

		extMgr = NetworkExtensionManager.getInstance();
		pm.sendPatch(fileID, patches, response -> {
			if (response.getStatus() != 200) {
				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_CHANGE_REQUEST_ID,
						IFileChangeResponse.class);
				for (ICoreExtension e : extensions) {
					IFileChangeResponse p = (IFileChangeResponse) e;
					p.fileChangeFailed(fileID, null);
				}
			} else {
				long version = ((FileChangeResponse) response.getData()).fileVersion;
				String acceptedPatches = ((FileChangeResponse) response.getData()).changes;
				String[] missingPatches = ((FileChangeResponse) response.getData()).missingPatches;

				Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.FILE_CHANGE_REQUEST_ID,
						IFileChangeResponse.class);
				for (ICoreExtension e : extensions) {
					IFileChangeResponse p = (IFileChangeResponse) e;
					p.fileChanged(fileID, new Patch(acceptedPatches), Patch.getPatches(missingPatches), version);
				}
			}
		}, null);
	}
}
