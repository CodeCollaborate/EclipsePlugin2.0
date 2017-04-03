package org.code.toboggan.modelmgr.integration.requests.project;

import java.io.IOException;
import java.nio.file.Path;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectGetFiles;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.modelmgr.integration.AbstractTest;
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
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.ProjectGetFilesRequest;
import clientcore.websocket.models.responses.ProjectGetFilesResponse;

public class TestProjectGetFiles extends AbstractTest {
	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();
		createDefaultTestProject();
		registerDefaultProject();
	}

	@After
	public void cleanup() throws CoreException {
		deleteTestProject();
	}

	@Test
	public void testSuccessfulPath() throws InterruptedException {
		// Run API call
		ProjectGetFiles pGetFiles = new ProjectGetFiles(APIExtensionManager.getInstance(), testProject.getProjectID());
		pGetFiles.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectGetFilesRequest)) {
					return false;
				}

				req = argument;

				ProjectGetFilesRequest projGetFilesReq = (ProjectGetFilesRequest) req.data;
				return projGetFilesReq.getProjectID() == testProject.getProjectID();
			}
		}));
		IResponseData data = new ProjectGetFilesResponse(testFiles);
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// Get IProject reference
		IProject iProj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProject.getName());

		// Validate that metadata was created
		SessionStorage ss = CoreActivator.getSessionStorage();

		for (File fData : testFiles) {
			Path filePath = iProj.getLocation().toFile().toPath()
					.resolve(fData.getRelativePath().resolve(fData.getFilename()));

			Assert.assertNotNull("File could not be found by ID in sessionStorage", ss.getFile(fData.getFileID()));
			Assert.assertNotNull("File could not be found by filepath in sessionStorage", ss.getFile(filePath));
			Assert.assertNotNull("File could not be found by ID in project metadata",
					ss.getProject(testProject.getProjectID()).getFile(fData.getFileID()));
			Assert.assertNotNull("File could not be found by filepath in project metadata",
					ss.getProject(testProject.getProjectID()).getFile(filePath));
		}
	}
}
