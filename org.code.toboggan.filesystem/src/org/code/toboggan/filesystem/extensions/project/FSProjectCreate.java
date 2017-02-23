package org.code.toboggan.filesystem.extensions.project;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.core.extension.APIExtensionIDs;
import org.code.toboggan.core.extension.AbstractExtensionManager;
import org.code.toboggan.core.extension.ICoreExtension;
import org.code.toboggan.filesystem.CCIgnore;
import org.code.toboggan.filesystem.extensionpoints.project.IFSProjectCreateExt;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.filesystem.utils.FSUtils;
import org.code.toboggan.network.request.extensionpoints.project.IProjectCreateResponse;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import clientcore.dataMgmt.ProjectController;
import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.Project;

public class FSProjectCreate implements IProjectCreateResponse {
	private static Logger logger = LogManager.getLogger(FSProjectCreate.class);

	private SessionStorage ss;
	private ProjectController pc;
	private AbstractExtensionManager extMgr;
	
	public FSProjectCreate() {
		this.ss = CoreActivator.getSessionStorage();
		pc = new ProjectController(ss);
		this.extMgr = FileSystemExtensionManager.getInstance();
	}
	
	@Override
	public void projectFetched(Project p) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject iProject = workspace.getRoot().getProject(p.getName());

		pc.createProject(p);
		pc.putProjectLocation(iProject.getLocation().toFile().toPath(), p.getProjectID());
		
		Display.getDefault().syncExec(() -> PlatformUI.getWorkbench().saveAllEditors(false));	
		CCIgnore ignoreFile = CCIgnore.createForProject(iProject);
		
		List<IFile> ifiles = recursivelyGetFiles(iProject, ignoreFile);
		for (IFile f : ifiles) {
			try (InputStream in = f.getContents();) {
				String contents = new String(FSUtils.inputStreamToByteArray(in));
				if (contents.contains("\r\n")) {
					contents = contents.replace("\r\n", "\n");
				}
				Path fileLocation = f.getLocation().toFile().toPath();
				new Thread(APIFactory.createFileCreate(f.getName(), fileLocation, p.getProjectID(), contents.getBytes())).start();
			} catch (IOException | CoreException e) {
				logger.error("Error reading files as part of project create", e);
				new Thread(APIFactory.createFileDelete(p.getProjectID())).start();
				return;
			}			
		}
		
		Set<ICoreExtension> extensions = extMgr.getExtensions(APIExtensionIDs.PROJECT_CREATE_ID);
		for (ICoreExtension e : extensions) {
			IFSProjectCreateExt createExt = (IFSProjectCreateExt) e;
			createExt.projectCreated(p, iProject);
		}
	}
	
	private List<IFile> recursivelyGetFiles(IContainer f, CCIgnore ignoreFile) {
		List<IFile> files = new ArrayList<>();
		IResource[] members = null;
		
		try {
			members = f.members();
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		
		for(IResource m : members) {
			if (m instanceof IFile) {
				String path = ((IFile) m).getProjectRelativePath().toString();
				if (ignoreFile.containsEntry(path)) {
					logger.debug(String.format("File %s was ignored when scanning for files.", path));
				} else {
					files.add((IFile) m);
				}
			} else if (m instanceof IFolder) {
				String path = ((IFolder) m).getProjectRelativePath().toString();
				if (ignoreFile.containsEntry(path)) {
					logger.debug(String.format("Folder %s was ignored when scanning for files.", path));
				} else {
					files.addAll(recursivelyGetFiles((IFolder) m, ignoreFile));
				}
			}
		}
		return files;
	}

	@Override
	public void projectFetchFailed(long projectId) {
		// Do nothing
	}
	
	@Override
	public void projectCreated(long projectId) {
		// Do nothing
	}

	@Override
	public void projectCreationFailed(String name) {
		// Do nothing
	}

	@Override
	public void subscribed(long projectId) {
		// Do nothing
	}

	@Override
	public void subscribeFailed(long projectId) {
		// Do nothing
	}
	
}
