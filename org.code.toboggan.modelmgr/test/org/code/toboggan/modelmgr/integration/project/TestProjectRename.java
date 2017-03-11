package org.code.toboggan.modelmgr.integration.project;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectRename;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectRename;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.request.extensions.project.NetworkProjectRename;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.File;
import clientcore.websocket.models.IResponseData;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.ProjectRenameRequest;
import clientcore.websocket.models.responses.ProjectRenameResponse;

public class TestProjectRename extends AbstractTest {
	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();
		createDefaultTestProject();
		createDefaultTestFiles();
		registerDefaultProject();
		registerDefaultFiles();
	}

	@After
	public void cleanup() throws CoreException {
		deleteTestProject();
	}

	@Test
	public void testSuccessfulPath() throws InterruptedException, CoreException {
		// Rename & Move project
		testProject.setName(testProject.getName() + "_renamed");
		Path iProjOldPath = testIProject.getLocation().toFile().toPath();

		Semaphore sem = new Semaphore(0);
		IResourceChangeListener listener = new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent arg0) {
				sem.release();
			}
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
		// Move backing project
		IProjectDescription iProjDesc = ResourcesPlugin.getWorkspace().newProjectDescription(testProject.getName());
		testIProject.move(iProjDesc, true, new NullProgressMonitor());

		Assert.assertTrue("Did not find resourceChangeEvent in timeout", sem.tryAcquire(5, TimeUnit.SECONDS));	
		testIProject = ResourcesPlugin.getWorkspace().getRoot().getProject(testProject.getName());		
		
		// Run API call
		ProjectRename pDelete = new ProjectRename(APIExtensionManager.getInstance(), testProject.getProjectID(),
				testProject.getName(), testIProject.getLocation().toFile().toPath());
		pDelete.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectRenameRequest)) {
					return false;
				}

				req = argument;

				ProjectRenameRequest projRenameReq = (ProjectRenameRequest) req.data;
				return projRenameReq.getProjectID() == testProject.getProjectID()
						&& projRenameReq.getNewName() == testProject.getName();
			}
		}));
		IResponseData data = new ProjectRenameResponse();
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		

		// Validate that metadata was created
		SessionStorage ss = CoreActivator.getSessionStorage();

		Path iProjNewPath = testIProject.getLocation().toFile().toPath();
		Project projMeta = ss.getProject(testProject.getProjectID());

		Assert.assertNotNull("Project metadata could not be found by ID", projMeta);
		Assert.assertNull("Project metadata was still found by old path", ss.getProject(iProjOldPath));
		Assert.assertNotNull("Project metadata could not be found by new path", ss.getProject(iProjNewPath));

		for (File fData : testFiles) {
			Path filePath = testIProject.getLocation().toFile().toPath()
					.resolve(fData.getRelativePath().resolve(fData.getFilename()));

			Assert.assertNotNull("File was null when searched by ID: " + fData.getFilename(),
					ss.getFile(fData.getFileID()));
			Assert.assertNotNull("File was null when searched by filepath: " + fData.getFilename(),
					ss.getFile(filePath));
		}
	}
}
