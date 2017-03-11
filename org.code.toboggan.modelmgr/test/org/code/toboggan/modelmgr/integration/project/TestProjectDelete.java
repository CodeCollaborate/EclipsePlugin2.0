package org.code.toboggan.modelmgr.integration.project;

import java.io.IOException;
import java.nio.file.Path;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectDelete;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectDelete;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.request.extensions.project.NetworkProjectDelete;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import clientcore.websocket.models.requests.ProjectDeleteRequest;
import clientcore.websocket.models.responses.ProjectDeleteResponse;

public class TestProjectDelete extends AbstractTest {
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
	public void testSuccessfulPath() throws InterruptedException {
		// Run API call
		ProjectDelete pDelete = new ProjectDelete(APIExtensionManager.getInstance(), testProject.getProjectID());
		pDelete.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectDeleteRequest)) {
					return false;
				}
				
				req = argument;

				ProjectDeleteRequest projDeleteReq = (ProjectDeleteRequest) req.data;
				return projDeleteReq.getProjectID() == testProject.getProjectID();
			}
		}));
		IResponseData data = new ProjectDeleteResponse();
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// Get IProject reference
		IProject iProj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProject.getName());

		// Validate that metadata was created
		SessionStorage ss = CoreActivator.getSessionStorage();
		Path iProjPath = iProj.getLocation().toFile().toPath();
		Project projMeta = ss.getProject(testProject.getProjectID());

		Assert.assertNull("Project metadata could not be found by ID", projMeta);
		Assert.assertNull("Project metadata could not be found by path", ss.getProject(iProjPath));

		for (File fData : testFiles) {
			Path filePath = iProj.getLocation().toFile().toPath()
					.resolve(fData.getRelativePath().resolve(fData.getFilename()));

			Assert.assertNull("File was not null when searched by ID: " + fData.getFilename(),
					ss.getFile(fData.getFileID()));
			Assert.assertNull("File was not null when searched by filepath: " + fData.getFilename(),
					ss.getFile(filePath));
		}
	}
}
