package org.code.toboggan.network.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;

public class NetworkUtils {
	private static Logger logger = LogManager.getLogger(NetworkUtils.class);

	/**
	 * Relativizes a file location against a project location (the absolute
	 * paths on the file system), removes the filename, and returns this in
	 * string format.
	 * 
	 * @param projectLocation
	 *            The absolute path of the project root
	 * @param fileLocation
	 *            The absolute path of the file
	 * @return The string of the relativized path with no filename
	 */
	public static String toStringRelativePath(Path projectLocation, Path fileLocation) {
		Path projectRelativePath = projectLocation.relativize(fileLocation);
		if (projectRelativePath.getParent() == null) {
			return "";
		}
		String stringProjectRelative = projectRelativePath.getParent().normalize().toString().replace('\\', '/');
		logger.debug("Project relativized path: " + stringProjectRelative);
		return stringProjectRelative;
	}

	/**
	 * Turns a relative path and a filename into an absolute path.
	 * 
	 * @param relativePath
	 * @param filename
	 * @return The Path object of the absolute path.
	 */
	public static Path toAbsolutePathFromRelative(Path relativePath, String filename) {
		Path wsPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().toPath();
		Path relativePathWithName = Paths.get(relativePath.toString(), filename);
		Path abs = Paths.get(wsPath.toString(), relativePathWithName.toString());
		logger.debug("Absolute path: " + abs.toString());
		return abs;
	}
}
