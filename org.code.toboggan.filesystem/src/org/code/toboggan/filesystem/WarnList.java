package org.code.toboggan.filesystem;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarnList {
	public HashMap<Path, List<Class<?>>> fileDirectoryWatchWarnList = new HashMap<>();
	public HashMap<String, List<Class<?>>> projectDirectoryWatchWarnList = new HashMap<>();

	public boolean isFileInWarnList(Path workspaceRelativePath, Class<?> notificationType) {
		if (fileDirectoryWatchWarnList.containsKey(workspaceRelativePath)) {
			return fileDirectoryWatchWarnList.get(workspaceRelativePath).contains(notificationType);
		}
		return false;
	}

	public void putFileInWarnList(Path fullFilePath, Class<?> notificationType) {
		if (fileDirectoryWatchWarnList.containsKey(fullFilePath)) {
			List<Class<?>> notificationTypes = fileDirectoryWatchWarnList.get(fullFilePath);
			notificationTypes.add(notificationType);
			fileDirectoryWatchWarnList.put(fullFilePath, notificationTypes);
		} else {
			List<Class<?>> notificationTypes = new ArrayList<>();
			notificationTypes.add(notificationType);
			fileDirectoryWatchWarnList.put(fullFilePath, notificationTypes);
		}
	}

	public void removeFileFromWarnList(Path fullFilePath, Class<?> notificationType) {
		if (fileDirectoryWatchWarnList.containsKey(fullFilePath)) {
			fileDirectoryWatchWarnList.get(fullFilePath).remove(notificationType);
		}
	}

	public boolean isProjectInWarnList(String projectName, Class<?> notificationType) {
		if (projectDirectoryWatchWarnList.containsKey(projectName)) {
			return projectDirectoryWatchWarnList.get(projectName).contains(notificationType);
		}
		return false;
	}

	public void putProjectInWarnList(String projectName, Class<?> notificationType) {
		if (projectDirectoryWatchWarnList.containsKey(projectName)) {
			List<Class<?>> notificationTypes = projectDirectoryWatchWarnList.get(projectName);
			notificationTypes.add(notificationType);
			projectDirectoryWatchWarnList.put(projectName, notificationTypes);
		} else {
			List<Class<?>> notificationTypes = new ArrayList<>();
			notificationTypes.add(notificationType);
			projectDirectoryWatchWarnList.put(projectName, notificationTypes);
		}
	}

	public void removeProjectFromWarnList(String projectName, Class<?> notificationType) {
		if (projectDirectoryWatchWarnList.containsKey(projectName)) {
			projectDirectoryWatchWarnList.get(projectName).remove(notificationType);
		}
	}
}
