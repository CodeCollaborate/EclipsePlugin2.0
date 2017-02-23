package org.code.toboggan.filesystem.extensions.file;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.filesystem.util.FSUtils;
import org.code.toboggan.network.NetworkActivator;
import org.code.toboggan.network.request.extensionpoints.file.IFilePullDiffSendChangesResponse;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;

import clientcore.dataMgmt.SessionStorage;
import clientcore.patching.Diff;
import clientcore.patching.Patch;
import clientcore.patching.PatchManager;
import clientcore.websocket.models.File;
import clientcore.websocket.models.responses.FileChangeResponse;

public class FSFilePullDiffSendChanges implements IFilePullDiffSendChangesResponse {
	private static Logger logger = LogManager.getLogger(FSFilePullDiffSendChanges.class);
	
	private SessionStorage ss;
	private PatchManager pm;
	
	public FSFilePullDiffSendChanges() {
		this.ss = CoreActivator.getSessionStorage();
		this.pm = NetworkActivator.getPatchManager();
	}
	
	@Override
	public void filePulled(long fileID, byte[] serverContents, String[] changes) {
		
		File file = ss.getFile(fileID);
		Path projectLocation = ss.getProjectLocation(file.getProjectID());
		Path fileLocation = Paths.get(projectLocation.toString(), file.getRelativePath().toString(), file.getFilename());
		
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
				new org.eclipse.core.runtime.Path(fileLocation.toString()));
		
		try {
			InputStream in = iFile.getContents();
			byte[] localContents = FSUtils.inputStreamToByteArray(in);
			String localStringContents = new String(localContents);
			localStringContents = localStringContents.replace("\r\n", "\n");
			in.close();
			// applying patches
			String serverStringContents = new String(serverContents);
			List<Patch> patches = new ArrayList<>();
			for (String stringPatch : changes) {
				patches.add(new Patch(stringPatch));
			}
			serverStringContents = pm.applyPatch(serverStringContents, patches);
			
			List<Diff> diffs = generateStringDiffs(serverStringContents, localStringContents);
			
			if (diffs != null && !diffs.isEmpty()) {
				pm.sendPatch(fileID, new Patch[] { new Patch((int) file.getFileVersion(), diffs)}, response -> {
                    synchronized (file) {
                        long version = ((FileChangeResponse) response.getData()).getFileVersion();
                        if (version == 0) {
                        	logger.error("File version returned from server was 0");
                        }
                        file.setFileVersion(version);
                    }
                }, null);
			} else {
				logger.debug("File either failed to pull or no diffs were found.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Diff> generateStringDiffs(String oldContents, String newContents) {
		DiffMatchPatch dmp = new DiffMatchPatch();
		List<org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Patch> patches = dmp.patchMake(oldContents, newContents);
		List<Diff> ccDiffs = new ArrayList<>();

		for (org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Patch p : patches) {
			int index = p.start1;
			
			for (org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff d : p.diffs) {
				Diff ccDiff = null;
				
				if (d.operation == DiffMatchPatch.Operation.INSERT) {
					ccDiff = new Diff(true, index, d.text);
					index += d.text.length();
				} else if (d.operation == DiffMatchPatch.Operation.DELETE) {
					ccDiff = new Diff(false, index, d.text);
				} else if (d.operation == DiffMatchPatch.Operation.EQUAL) {
					index += d.text.length();
				}
				
				if (ccDiff != null) {
					ccDiff.convertToLF(oldContents);
					ccDiffs.add(ccDiff);
				}
			}
			
		}
		
		return ccDiffs;
	}

	@Override
	public void filePullFailed(long fileID) {

	}

}
