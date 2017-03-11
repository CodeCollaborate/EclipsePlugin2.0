package org.code.toboggan.modelmgr.integration.project;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectSubscribe;
import org.code.toboggan.core.extensionpoints.APIExtensionIDs;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.filesystem.extensions.FileSystemExtensionManager;
import org.code.toboggan.filesystem.extensions.project.FSProjectSubscribe;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectFetchAndSubscribeAll;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectGetFiles;
import org.code.toboggan.modelmgr.extensions.project.ModelMgrProjectSubscribe;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;
import org.code.toboggan.network.request.extensions.file.NetworkFilePull;
import org.code.toboggan.network.request.extensions.project.NetworkProjectSubscribe;
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
import clientcore.websocket.models.requests.FilePullRequest;
import clientcore.websocket.models.requests.ProjectGetFilesRequest;
import clientcore.websocket.models.requests.ProjectSubscribeRequest;
import clientcore.websocket.models.responses.FilePullResponse;
import clientcore.websocket.models.responses.ProjectGetFilesResponse;
import clientcore.websocket.models.responses.ProjectSubscribeResponse;

public class TestProjectSubscribe extends AbstractTest {
	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();
		new ModelMgrProjectFetchAndSubscribeAll().fetchedAll(Arrays.asList(new Project[]{testProject}));
	}

	@After
	public void cleanup() throws CoreException {
		deleteTestProject();
	}

	@Test
	public void testSuccessfulPath() throws InterruptedException {
		// Run API call
		ProjectSubscribe pSubscribe = new ProjectSubscribe(APIExtensionManager.getInstance(),
				testProject.getProjectID());
		pSubscribe.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectSubscribeRequest)) {
					return false;
				}

				req = argument;

				ProjectSubscribeRequest projSubscribeReq = (ProjectSubscribeRequest) req.data;
				return projSubscribeReq.getProjectID() == testProject.getProjectID();
			}
		}));
		IResponseData data = new ProjectSubscribeResponse();
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		// Verify project GetFiles
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
		data = new ProjectGetFilesResponse(testFiles);
		resp = new Response(0L, 200, data);

		// Start next part
		req.getResponseHandler().handleResponse(resp);

		// Get SessionStorage
		SessionStorage ss = CoreActivator.getSessionStorage();

		// Verify IProject and metadata are created
		testIProject = ResourcesPlugin.getWorkspace().getRoot().getProject(testProject.getName());

		Assert.assertNotNull("IProject was null - no eclipse project was created", testIProject);
		Assert.assertNotNull("Project metadata was null when retrieved by ID",
				ss.getProject(testProject.getProjectID()));
		Assert.assertNotNull("Project metadata was null when retrieved by filepath",
				ss.getProject(testIProject.getLocation().toFile().toPath()));

		for (File fData : testFiles) {
			Path filePath = testIProject.getLocation().toFile().toPath()
					.resolve(fData.getRelativePath().resolve(fData.getFilename()));

			Assert.assertNotNull("File was null when searched by ID: " + fData.getFilename(),
					ss.getFile(fData.getFileID()));
			Assert.assertNotNull("File was null when searched by filepath: " + fData.getFilename(),
					ss.getFile(filePath));
		}

		CoreActivator.getExecutor().shutdown();
		Assert.assertTrue("Timed out waiting for executor to shutdown",
				CoreActivator.getExecutor().awaitTermination(5, TimeUnit.SECONDS));

		// Verify that all File Pull requests are sent
		for (int i = 0; i < testFiles.length; i++) {
			File fData = testFiles[i];
			Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
				@Override
				public boolean matches(Request argument) {
					if (!(argument.data instanceof FilePullRequest)) {
						return false;
					}

					req = argument;

					FilePullRequest filePullRequest = (FilePullRequest) req.data;
					return filePullRequest.getFileID() == fData.getFileID();
				}
			}));
			data = new FilePullResponse(testFileContents[i].replace("\r\n", "\n").getBytes(), new String[]{});
			resp = new Response(0L, 200, data);

			// Send Response
			req.getResponseHandler().handleResponse(resp);
		}
	}
}
