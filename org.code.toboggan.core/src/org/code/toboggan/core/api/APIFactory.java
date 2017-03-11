package org.code.toboggan.core.api;

import java.nio.file.Path;
import java.util.List;

import org.code.toboggan.core.api.file.FileChange;
import org.code.toboggan.core.api.file.FileCreate;
import org.code.toboggan.core.api.file.FileDelete;
import org.code.toboggan.core.api.file.FileMove;
import org.code.toboggan.core.api.file.FilePull;
import org.code.toboggan.core.api.file.FilePullDiffSendChanges;
import org.code.toboggan.core.api.file.FileRename;
import org.code.toboggan.core.api.project.ProjectCreate;
import org.code.toboggan.core.api.project.ProjectDelete;
import org.code.toboggan.core.api.project.ProjectFetchAndSubscribeAll;
import org.code.toboggan.core.api.project.ProjectGetFiles;
import org.code.toboggan.core.api.project.ProjectGetPermissionConstants;
import org.code.toboggan.core.api.project.ProjectGrantPermissions;
import org.code.toboggan.core.api.project.ProjectLookup;
import org.code.toboggan.core.api.project.ProjectRename;
import org.code.toboggan.core.api.project.ProjectRevokePermissions;
import org.code.toboggan.core.api.project.ProjectSubscribe;
import org.code.toboggan.core.api.project.ProjectUnsubscribe;
import org.code.toboggan.core.api.user.UserLogin;
import org.code.toboggan.core.api.user.UserLookup;
import org.code.toboggan.core.api.user.UserProjects;
import org.code.toboggan.core.api.user.UserRegister;
import org.code.toboggan.core.extensionpoints.APIExtensionManager;

import clientcore.patching.Patch;

public class APIFactory {
	// Projects
	public static ProjectCreate createProjectCreate(String name) {
		return new ProjectCreate(APIExtensionManager.getInstance(), name);
	}
	
	public static ProjectDelete createProjectDelete(long projectID) {
		return new ProjectDelete(APIExtensionManager.getInstance(), projectID);
	}
	
	public static ProjectGetFiles createProjectGetFiles(long projectID) {
		return new ProjectGetFiles(APIExtensionManager.getInstance(), projectID);
	}
	
	public static ProjectGetPermissionConstants createProjectGetPermissionConstants() {
		return new ProjectGetPermissionConstants(APIExtensionManager.getInstance());
	}
	
	public static ProjectGrantPermissions createProjectGrantPermissions(long projectID, String grantUsername, int permission) {
		return new ProjectGrantPermissions(APIExtensionManager.getInstance(), projectID, grantUsername, permission);
	}
	
	public static ProjectLookup createProjectLookup(List<Long> projectIDs) {
		return new ProjectLookup(APIExtensionManager.getInstance(), projectIDs);
	}
	
	public static ProjectRename createProjectRename(long projectID, String newName, Path newLocation) {
		return new ProjectRename(APIExtensionManager.getInstance(), projectID, newName, newLocation);
	}
	
	public static ProjectRevokePermissions createRevokePermissions(long projectID, String username) {
		return new ProjectRevokePermissions(APIExtensionManager.getInstance(), projectID, username);
	}
	
	public static ProjectSubscribe createProjectSubscribe(long projectID) {
		return new ProjectSubscribe(APIExtensionManager.getInstance(), projectID);
	}
	
	public static ProjectUnsubscribe createProjectUnsubscribe(long projectID) {
		return new ProjectUnsubscribe(APIExtensionManager.getInstance(), projectID);
	}
	
	public static ProjectFetchAndSubscribeAll createProjectFetchAndSubscribeAll(List<Long> projectIDs) {
		return new ProjectFetchAndSubscribeAll(APIExtensionManager.getInstance(), projectIDs);
	}
	
	// Files
	public static FileChange createFileChange(long fileID, Patch[] patches, String fileContents) {
		return new FileChange(APIExtensionManager.getInstance(), fileID, patches, fileContents);
	}
	
	public static FileCreate createFileCreate(String name, Path absolutePath, long projectID, byte[] fileBytes) {
		return new FileCreate(APIExtensionManager.getInstance(), name, absolutePath, projectID, fileBytes);
	}
	
	public static FileDelete createFileDelete(long fileID) {
		return new FileDelete(APIExtensionManager.getInstance(), fileID);
	}
	
	public static FileMove createFileMove(long fileID, Path oldAbsolutePath, Path newAbsolutePath) {
		return new FileMove(APIExtensionManager.getInstance(), fileID, oldAbsolutePath, newAbsolutePath);
	}
	
	public static FilePull createFilePull(long fileID) {
		return new FilePull(APIExtensionManager.getInstance(), fileID);
	}
	
	public static FilePullDiffSendChanges createFilePullDiffSendChanges(long fileID) {
		return new FilePullDiffSendChanges(APIExtensionManager.getInstance(), fileID);
	}
	
	public static FileRename createFileRename(long fileID, Path oldAbsolutePath, Path newAbsolutePath, String newName) {
		return new FileRename(APIExtensionManager.getInstance(), fileID, oldAbsolutePath, newAbsolutePath, newName);
	}
	
	// Users
	public static UserLogin createUserLogin(String username, String password) {
		return new UserLogin(APIExtensionManager.getInstance(), username, password);
	}
	
	public static UserLookup createUserLookup(String username) {
		return new UserLookup(APIExtensionManager.getInstance(), username);
	}
	
	public static UserProjects createUserProjects() {
		return new UserProjects(APIExtensionManager.getInstance());
	}
	
	public static UserRegister createUserRegister(String username, String firstName, String lastName, String password, String email) {
		return new UserRegister(APIExtensionManager.getInstance(), username, firstName, lastName, email, password);
	}
}
