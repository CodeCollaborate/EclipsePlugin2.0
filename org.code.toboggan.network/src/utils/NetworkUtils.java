package utils;

import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkUtils {
	private static Logger logger = LogManager.getLogger(NetworkUtils.class);

	/**
	 * Relativizes a file location against a project location (the absolute
	 * paths on the file system), removes the filename, and returns this in
	 * string format.
	 * 
	 * @param projectLocation
	 * 		The absolute path of the project root
	 * @param fileLocation
	 * 		The absolute path of the file
	 * @return
	 * 		The string of the relativized path with no filename
	 */
	public static String toStringRelativePath(Path projectLocation, Path fileLocation) {
		Path projectRelativePath = projectLocation.relativize(fileLocation);
		String stringProjectRelative = FilenameUtils.getPath(projectRelativePath.toString());
		logger.debug("Project relativized path: " + stringProjectRelative);
		return stringProjectRelative;
	}
	
}
