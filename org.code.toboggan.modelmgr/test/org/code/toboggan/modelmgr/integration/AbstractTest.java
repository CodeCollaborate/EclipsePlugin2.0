package org.code.toboggan.modelmgr.integration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.core.extensionpoints.AbstractExtensionManager;
import org.code.toboggan.filesystem.FSActivator;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.filesystem.extensions.project.FSProjectCreate;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectCreate;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectGetFiles;
import org.code.toboggan.network.NetworkActivator;
import org.code.toboggan.network.WSService;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.mockito.Mockito;

import com.google.common.collect.HashBiMap;

import clientcore.patching.PatchManager;
import clientcore.websocket.WSManager;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Permission;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.requests.FileCreateRequest;

public abstract class AbstractTest {
	private final String DEFAULT_TEST_USER = "testUser";
	private final Project DEFAULT_TEST_PROJECT = new Project(100, "testProj1", new HashMap<>());
	private final File[] DEFAULT_TEST_FILES = new File[] {
			new File(1, "testFile1", "testDir1/", 1, DEFAULT_TEST_USER, "NOW"),
			new File(2, "testFile2", "testDir2/", 1, DEFAULT_TEST_USER, "NOW"),
			new File(3, "testFile3", "testDir3/testDir4/", 1, DEFAULT_TEST_USER, "NOW") };
	private final String[] DEFAULT_TEST_FILE_CONTENTS = new String[] { "testFile1", "test\nFile2", "test\r\nFile3" };

	protected WSManager wsMgr;
	protected String testUser = DEFAULT_TEST_USER;
	protected Project testProject = DEFAULT_TEST_PROJECT;
	protected IProject testIProject;
	protected File[] testFiles = DEFAULT_TEST_FILES;
	protected String[] testFileContents = DEFAULT_TEST_FILE_CONTENTS;
	protected Request req;

	protected void resetExtensionsAndBuildStandardMocks() {
		// Clean any extension handlers.
		CoreActivator.reset();
		NetworkActivator.reset();
		FSActivator.reset();

		AbstractExtensionManager.resetAll();

		// Setup WebSocket service and manager
		wsMgr = Mockito.mock(WSManager.class);
		WSService wsServiceMock = Mockito.mock(WSService.class);
		Mockito.when(wsServiceMock.getWSManager()).thenReturn(wsMgr);
		NetworkActivator.setWSService(wsServiceMock);
		NetworkActivator.getPatchManager().setWsMgr(wsMgr);
		PatchManager.notifyOnSend = true;
		NetworkActivator.registerNotificationHandlers();

		FileSystemExtensionManager.getInstance();
		NetworkExtensionManager.getInstance();
		APIExtensionManager.getInstance();

		CoreActivator.getSessionStorage().removeAllPropertyChangeListeners();
		FSActivator.deregisterResourceListeners();
	}

	private void createFolders(File file, IPath relPath, IProject p) throws CoreException {
		NullProgressMonitor progressMonitor = new NullProgressMonitor();

		if (!relPath.toString().equals("") && !relPath.toString().equals(".")) {

			IPath currentFolder = org.eclipse.core.runtime.Path.EMPTY;
			for (int i = 0; i < relPath.segmentCount(); i++) {
				// iterate through path segments and create if they don't exist
				currentFolder = currentFolder.append(relPath.segment(i));

				IFolder newFolder = p.getFolder(currentFolder);
				if (!newFolder.exists()) {
					newFolder.create(true, true, progressMonitor);
				}
			}
		}
	}

	protected void createDefaultTestProject() throws CoreException, IOException {
		createTestProject(DEFAULT_TEST_USER, DEFAULT_TEST_PROJECT);
	}

	protected void createTestProject(String testUser, Project testProject) throws CoreException, IOException {
		IProjectDescription iProjDesc = ResourcesPlugin.getWorkspace().newProjectDescription(testProject.getName());
		this.testIProject = ResourcesPlugin.getWorkspace().getRoot().getProject(testProject.getName());
		testIProject.create(iProjDesc, new NullProgressMonitor());
		testIProject.open(new NullProgressMonitor());

	}

	protected void createDefaultTestFiles() throws CoreException, IOException {
		createTestFiles(DEFAULT_TEST_FILES, DEFAULT_TEST_FILE_CONTENTS);
	}

	protected void createTestFiles(File[] testFiles, String[] testFileContents) throws CoreException {
		for (int i = 0; i < testFiles.length; i++) {
			File fData = testFiles[i];
			String fContent = testFileContents[i];

			fData.setProjectID(testProject.getProjectID());

			Path filePath = fData.getRelativePath().resolve(fData.getFilename());
			IFile iFile = testIProject.getFile(filePath.toString());

			IPath fileDirectory = iFile.getProjectRelativePath().removeLastSegments(1);
			createFolders(fData, fileDirectory, testIProject);

			FSActivator.getWarnList().putFileInWarnList(iFile.getLocation().toFile().toPath(), FileCreateRequest.class);

			InputStream source = new ByteArrayInputStream(fContent.getBytes());
			iFile.create(source, IFile.FORCE, null);
		}
	}

	protected void registerDefaultProject() throws CoreException, IOException {
		registerProject(DEFAULT_TEST_PROJECT);
	}

	protected void registerProject(Project testProject) throws CoreException, IOException {
		new ModelMgrProjectCreate().projectCreated(testProject, testIProject);
		new FSProjectCreate().projectFetched(testProject);
	}

	protected void registerDefaultFiles() throws CoreException, IOException {
		registerFiles(DEFAULT_TEST_FILES, DEFAULT_TEST_FILE_CONTENTS);
	}

	protected void registerFiles(File[] testFiles, String[] testFileContents) throws CoreException, IOException {
		new ModelMgrProjectGetFiles().projectGetFiles(testProject.getProjectID(), testFiles);

		// Add document shadows
		for (int i = 0; i < testFiles.length; i++) {
			File fData = testFiles[i];
			String fContent = testFileContents[i];

			FSActivator.getShadowDocumentManager().putShadow(fData.getFileID(), fContent.replace("\r\n", "\n"));
		}
	}

	protected void deleteTestProject() throws CoreException {
		this.testIProject = ResourcesPlugin.getWorkspace().getRoot().getProject(testProject.getName());
		testIProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		testIProject.delete(true, true, new NullProgressMonitor());
	}

	protected void setupUserProjectPermissions() {
		// Setup permissions constants
		Map<String, Integer> permissions = new HashMap<>();
		permissions.put("read", 1);
		permissions.put("write", 5);
		CoreActivator.getSessionStorage().setUsername(testUser);
		CoreActivator.getSessionStorage().setPermissionConstants(HashBiMap.create(permissions));
		testProject.getPermissions().put(testUser, new Permission(testUser, permissions.get("write"), testUser, "NOW"));
	}
}
