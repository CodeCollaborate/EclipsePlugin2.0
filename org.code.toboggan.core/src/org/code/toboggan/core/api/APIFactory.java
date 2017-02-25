package org.code.toboggan.core.api;

import java.nio.file.Path;
import java.util.List;

import org.code.toboggan.core.api.file.*;
import org.code.toboggan.core.api.project.*;
import org.code.toboggan.core.api.user.*;
import org.code.toboggan.core.extension.APIExtensionManager;
import org.code.toboggan.core.extension.AbstractExtensionManager;

import clientcore.patching.*;

public class APIFactory {
	
	private static final AbstractExtensionManager EXT_MGR = APIExtensionManager.getInstance();
	
	// Projects
	public static ProjectCreate createProjectCreate(String name) {
		return new ProjectCreate(EXT_MGR, name);
	}
	
	public static ProjectDelete createProjectDelete(long projectID) {
		return new ProjectDelete(EXT_MGR, projectID);
	}
	
	public static ProjectGetFiles createProjectGetFiles(long projectID) {
		return new ProjectGetFiles(EXT_MGR, projectID);
	}
	
	public static ProjectGetPermissionConstants createProjectGetPermissionConstants() {
		return new ProjectGetPermissionConstants(EXT_MGR);
	}
	
	public static ProjectGrantPermissions createProjectGrantPermissions(long projectID, String grantUsername, int permission) {
		return new ProjectGrantPermissions(EXT_MGR, projectID, grantUsername, permission);
	}
	
	public static ProjectLookup createProjectLookup(List<Long> projectIDs) {
		return new ProjectLookup(EXT_MGR, projectIDs);
	}
	
	public static ProjectRename createProjectRename(long projectID, String newName, Path newProjectLocation) {
		return new ProjectRename(EXT_MGR, projectID, newName, newProjectLocation);
	}
	
	public static ProjectRevokePermissions createRevokePermissions(long projectID, String username) {
		return new ProjectRevokePermissions(EXT_MGR, projectID, username);
	}
	
	public static ProjectSubscribe createProjectSubscribe(long projectID) {
		return new ProjectSubscribe(EXT_MGR, projectID);
	}
	
	public static ProjectUnsubscribe createProjectUnsubscribe(long projectID) {
		return new ProjectUnsubscribe(EXT_MGR, projectID);
	}
	
	public static ProjectFetchAndSubscribeAll createProjectFetchAndSubscribeAll(List<Long> projectIDs) {
		return new ProjectFetchAndSubscribeAll(EXT_MGR, projectIDs);
	}
	
	// Files
	public static FileChange createFileChange(long fileID, Patch[] patches) {
		return new FileChange(EXT_MGR, fileID, patches);
	}
	
	public static FileCreate createFileCreate(String name, Path absolutePath, long projectID, byte[] fileBytes) {
		return new FileCreate(EXT_MGR, name, absolutePath, projectID, fileBytes);
	}
	
	public static FileDelete createFileDelete(long fileID) {
		return new FileDelete(EXT_MGR, fileID);
	}
	
	public static FileMove createFileMove(long fileID, Path oldAbsolutePath, Path newAbsolutePath) {
		return new FileMove(EXT_MGR, fileID, oldAbsolutePath, newAbsolutePath);
	}
	
	public static FilePull createFilePull(long fileID) {
		return new FilePull(EXT_MGR, fileID);
	}
	
	public static FilePullDiffSendChanges createFilePullDiffSendChanges(long fileID) {
		return new FilePullDiffSendChanges(EXT_MGR, fileID);
	}
	
	public static FileRename createFileRename(long fileID, Path oldAbsolutePath, Path newAbsolutePath, String newName) {
		return new FileRename(EXT_MGR, fileID, oldAbsolutePath, newAbsolutePath, newName);
	}
	
	// Users
	public static UserLogin createUserLogin(String username, String password) {
		return new UserLogin(EXT_MGR, username, password);
	}
	
	public static UserLookup createUserLookup(String username) {
		return new UserLookup(EXT_MGR, username);
	}
	
	public static UserProjects createUserProjects() {
		return new UserProjects(EXT_MGR);
	}
	
	public static UserRegister createUserRegister(String username, String firstName, String lastName, String password, String email) {
		return new UserRegister(EXT_MGR, username, firstName, lastName, email, password);
	}
}
