package org.code.toboggan.modelmgr.integration.requests.project;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.project.ProjectCreate;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;
import org.code.toboggan.filesystem.CCIgnore;
import org.code.toboggan.modelmgr.integration.AbstractTest;
import org.eclipse.core.resources.IFile;
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
import clientcore.websocket.models.Permission;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.Request;
import clientcore.websocket.models.Response;
import clientcore.websocket.models.requests.FileCreateRequest;
import clientcore.websocket.models.requests.ProjectCreateRequest;
import clientcore.websocket.models.requests.ProjectLookupRequest;
import clientcore.websocket.models.requests.ProjectSubscribeRequest;
import clientcore.websocket.models.responses.FileCreateResponse;
import clientcore.websocket.models.responses.ProjectCreateResponse;
import clientcore.websocket.models.responses.ProjectLookupResponse;
import clientcore.websocket.models.responses.ProjectSubscribeResponse;

public class TestProjectCreate extends AbstractTest {
	@Before
	public void setup() throws CoreException, IOException {
		resetExtensionsAndBuildStandardMocks();
		createDefaultTestProject();
		createDefaultTestFiles();
	}

	@After
	public void cleanup() throws CoreException {
		deleteTestProject();
	}

	@Test
	public void testSuccessfulPath() throws InterruptedException {
		// Run API call
		ProjectCreate pCreate = new ProjectCreate(APIExtensionManager.getInstance(), testProject.getName());
		pCreate.execute();

		// Verify project request
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectCreateRequest)) {
					return false;
				}

				req = argument;

				ProjectCreateRequest projCreateReq = (ProjectCreateRequest) req.data;
				return projCreateReq.getName().equals(testProject.getName());
			}
		}));
		IResponseData data = new ProjectCreateResponse(testProject.getProjectID());
		Response resp = new Response(0L, 200, data);

		// Send Response
		req.getResponseHandler().handleResponse(resp);

		CoreActivator.getExecutor().shutdown();
		Assert.assertTrue("Timed out waiting for executor to shutdown",
				CoreActivator.getExecutor().awaitTermination(5, TimeUnit.SECONDS));
		CoreActivator.resetExecutor();

		// Verify project pull
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
		data = new ProjectSubscribeResponse();
		resp = new Response(0L, 200, data);

		// Start next part
		req.getResponseHandler().handleResponse(resp);

		// Verify project pull
		Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
			@Override
			public boolean matches(Request argument) {
				if (!(argument.data instanceof ProjectLookupRequest)) {
					return false;
				}

				req = argument;

				ProjectLookupRequest projLookupRequest = (ProjectLookupRequest) req.data;
				return projLookupRequest.getProjectIDs().size() == 1
						&& projLookupRequest.getProjectIDs().get(0) == testProject.getProjectID();
			}
		}));
		Map<String, Permission> permissionMap = new HashMap<String, Permission>();
		permissionMap.put(testUser, new Permission(testUser, 10, testUser, "10/10/2010"));
		Project[] projs = new Project[] {
				new Project(testProject.getProjectID(), testProject.getName(), permissionMap) };
		data = new ProjectLookupResponse(projs);
		resp = new Response(0L, 200, data);

		// Start next part
		req.getResponseHandler().handleResponse(resp);

		// Get IProject reference
		IProject iProj = ResourcesPlugin.getWorkspace().getRoot().getProject(testProject.getName());

		// Check that CCIgnore was created
		IFile ccIgnore = iProj.getFile(CCIgnore.FILENAME);
		Assert.assertTrue("CCIgnore not found in test project", ccIgnore.exists());

		// Validate that metadata was created
		SessionStorage ss = CoreActivator.getSessionStorage();
		Path iProjPath = iProj.getLocation().toFile().toPath();
		Project projMeta = ss.getProject(testProject.getProjectID());

		Assert.assertNotNull("Project metadata could not be found by ID", projMeta);
		Assert.assertNotNull("Project metadata could not be found by path", ss.getProject(iProjPath));
		Assert.assertEquals("Project metadata different for ID vs path", projMeta, ss.getProject(iProjPath));
		Assert.assertEquals("ProjectID was incorrect in metadata", testProject.getProjectID(), projMeta.getProjectID());
		Assert.assertEquals("ProjectName was incorrect in metadata", testProject.getName(), projMeta.getName());

		Assert.assertTrue("Project was not listed as subscribed",
				ss.getSubscribedIds().contains(projMeta.getProjectID()));

		CoreActivator.getExecutor().shutdown();
		Assert.assertTrue("Timed out waiting for executor to shutdown",
				CoreActivator.getExecutor().awaitTermination(5, TimeUnit.SECONDS));

		// Validate that fileCreateRequests were sent
		for (int i = 0; i < testFiles.length; i++) {
			File fData = testFiles[i];
			String expectedContent = testFileContents[i].replace("\r\n", "\n");
			Mockito.verify(wsMgr).sendAuthenticatedRequest(Mockito.argThat(new ArgumentMatcher<Request>() {
				@Override
				public boolean matches(Request argument) {
					if (!(argument.data instanceof FileCreateRequest)) {
						return false;
					}

					req = argument;

					FileCreateRequest fileCreateRequest = (FileCreateRequest) req.data;

					boolean res1 = fData.getFilename().equals(fileCreateRequest.getName());
					boolean res2 = fData.getRelativePath().equals(Paths.get(fileCreateRequest.getRelativePath()));
					boolean res3 = expectedContent.equals(new String(fileCreateRequest.getFileBytes()));
					return res1 && res2 && res3;
				}
			}));
			data = new FileCreateResponse(fData.getFileID());
			resp = new Response(0L, 200, data);

			// Send Response
			req.getResponseHandler().handleResponse(resp);
		}
	}
}
