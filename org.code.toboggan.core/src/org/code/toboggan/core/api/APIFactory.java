package org.code.toboggan.core.api;

import java.nio.file.Path;

import org.code.toboggan.core.api.file.FileChange;
import org.code.toboggan.core.api.file.FileCreate;
import org.code.toboggan.core.api.file.FileDelete;
import org.code.toboggan.core.api.file.FileMove;
import org.code.toboggan.core.api.file.FilePull;
import org.code.toboggan.core.api.file.FileRename;
import org.code.toboggan.core.api.project.ProjectCreate;
import org.code.toboggan.core.api.project.ProjectDelete;
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
import org.code.toboggan.core.extension.ExtensionManager;

import patching.*;

public class APIFactory {
	
	private static final ExtensionManager EXT_MGR = ExtensionManager.getInstance();
	
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
	
	public static ProjectLookup createProjectLookup(long projectID) {
		return new ProjectLookup(EXT_MGR, projectID);
	}
	
	public static ProjectRename createProjectRename(long projectID, String newName) {
		return new ProjectRename(EXT_MGR, projectID, newName);
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
	
	// Files
	public static FileChange createFileChange(long fileID, Patch[] patches) {
		return new FileChange(EXT_MGR, fileID, patches);
	}
	
	public static FileCreate createFileCreate(String name, Path workspaceRelativePath, long projectID, byte[] fileBytes) {
		return new FileCreate(EXT_MGR, name, workspaceRelativePath, projectID, fileBytes);
	}
	
	public static FileDelete createFileDelete(long fileID) {
		return new FileDelete(EXT_MGR, fileID);
	}
	
	public static FileMove createFileMove(long fileID, Path newWorkspaceRelativePath) {
		return new FileMove(EXT_MGR, fileID, newWorkspaceRelativePath);
	}
	
	public static FilePull createFilePull(long fileID) {
		return new FilePull(EXT_MGR, fileID);
	}
	
	public static FileRename createFileRename(long fileID, Path newWorkspaceRelativePath, String newName) {
		return new FileRename(EXT_MGR, fileID, newWorkspaceRelativePath, newName);
	}
	
	// Users
	public static UserLogin createUserLogin(String username, String password) {
		return new UserLogin(EXT_MGR, username, password);
	}
	
	public static UserLookup createUserLookup(String username) {
		return new UserLookup(EXT_MGR, username);
	}
	
	public static UserProjects createUserProjects(String username) {
		return new UserProjects(EXT_MGR, username);
	}
	
	public static UserRegister createUserRegister(String username, String firstName, String lastName, String password, String email) {
		return new UserRegister(EXT_MGR, username, firstName, lastName, email, password);
	}
}
