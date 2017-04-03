package org.code.toboggan.filesystem.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;

public class FSUtils {
	private static Logger logger = LogManager.getLogger(FSUtils.class);

	public static byte[] inputStreamToByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		byte curr;
		while (true) {
			curr = (byte) is.read();
			if (curr == -1)
				break;
			out.write(curr);
		}
		byte[] result = out.toByteArray();
		out.close();
		return result;
	}

	private static final Path workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().toPath();

	public static Path getLocationForRelativePath(Path workspaceRelativePath) {
		Path result = Paths.get(workspacePath.toString(), workspaceRelativePath.toString());
		logger.debug(String.format("Resolved relative path to [%s]", result.toString()));
		return result;
	}

}
