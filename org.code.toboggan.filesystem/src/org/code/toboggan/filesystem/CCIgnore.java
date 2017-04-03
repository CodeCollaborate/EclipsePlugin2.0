package org.code.toboggan.filesystem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.File;
import clientcore.websocket.models.Project;
import clientcore.websocket.models.responses.FileCreateResponse;

public class CCIgnore {

	public static String FILENAME = ".ccignore";
	public static String DEFAULT_CONTENTS = ".ccconfig\n" + ".idea\n" + ".gradle\n" + ".git\n" + ".svn\n"
			+ ".settings\n" + "\n" + "bin\n" + "build\n" + "target\n" + "out\n" + "\n" + "*.o\n" + "*.a\n" + "*.so\n"
			+ "*.exe\n" + "*.swp";

	private Set<String> ignoredFiles = new HashSet<>();
	private static final Logger logger = LogManager.getLogger("ccignore");
	private boolean initialized = false;

	/**
	 * Creates a new .ccignore file for the given project on disk. This creation
	 * will be detected by the directory watching system and sent to the server.
	 * Also loads the file into an instance of this class so that the contents
	 * can be checked with the provided handles.
	 * 
	 * @param p
	 * @return Returns a reference to the loaded
	 */
	public static CCIgnore createForProject(IProject p) {
		IFile file = p.getFile(FILENAME);

		if (!file.exists()) {
			InputStream in = new ByteArrayInputStream(DEFAULT_CONTENTS.getBytes());

			try {
				// warn directory watching
				Path fileLocation = file.getLocation().toFile().toPath();
				FSActivator.getWarnList().putFileInWarnList(fileLocation, FileCreateResponse.class);

				file.create(in, true, new NullProgressMonitor());
				in.close();
			} catch (CoreException e) {
				// TODO: re-hook up UI
				// MessageDialog.createDialog("Failed to generate .ccignore
				// file.").open();
				logger.error("Failed to generate .ccignore file", e);
			} catch (IOException e) {
				logger.error("Failed to close input stream.", e);
			}
		}

		CCIgnore ignoreFile = new CCIgnore();
		ignoreFile.loadCCIgnore(p);
		return ignoreFile;
	}

	/**
	 * Loads the contents of the .ccignore file for the given project into the
	 * Set encapsulated within this class.
	 * 
	 * @param p
	 */
	public void loadCCIgnore(IProject p) {
		IFile f = p.getFile(FILENAME);
		if (f.exists()) {
			try {
				synchronized (ignoredFiles) {
					ignoredFiles.clear();
					InputStream in = f.getContents();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line;
					while ((line = reader.readLine()) != null) {
						// ignore comments
						if (line.startsWith("#") || line.startsWith("//"))
							continue;

						// ignore empty lines
						if (line.equals(""))
							continue;

						ignoredFiles.add(line);
					}
					reader.close();
					initialized = true;
					(new Thread(() -> serverCleanUp(p))).start();
				}
			} catch (CoreException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Iterates through the file metadata for the given project and sends delete
	 * requests if the file is included in the ignore file.
	 * 
	 * @param p
	 * 
	 */
	private void serverCleanUp(IProject p) {
		if (!initialized) {
			return;
		}

		SessionStorage ss = CoreActivator.getSessionStorage();
		Path projectLocation = p.getLocation().toFile().toPath();
		Project pMeta = ss.getProject(projectLocation);

		if (pMeta == null) {
			return;
		}
		Collection<File> fileMetas = pMeta.getFiles();

		if (fileMetas == null) {
			return;
		}
		synchronized (ignoredFiles) {
			for (File fm : fileMetas) {
				String path = Paths.get(fm.getRelativePath().toString(), fm.getFilename()).normalize().toString();

				if (containsEntry(path)) {
					// send delete request for fileID
					logger.debug(String.format("Cleaning up [%s] from server", path));
					APIFactory.createFileDelete(fm.getFileID()).runAsync();
				}
			}
		}
	}

	/**
	 * Checks if the given entry is contained within the encapsulated set that
	 * has been loaded from the .ccignore file.
	 * 
	 * @param e
	 * @return Returns true if the encapsulated set contains the given entry.
	 */
	public boolean containsEntry(String e) {
		if (!initialized) {
			logger.warn("WARNING: ccignore queried before it was initialized");
			return false;
		}

		Path path = Paths.get(e).normalize();
		for (String rule : this.ignoredFiles) {
			if (rule.equals(path.toString())) {
				return true;
			}

			PathMatcher glob = FileSystems.getDefault().getPathMatcher("glob:" + rule);
			// need to also match with "**/" to follow .gitignore's path
			// glob-ing standard
			PathMatcher globAnywhere = FileSystems.getDefault().getPathMatcher("glob:" + "**/" + rule);
			PathMatcher globFolder = FileSystems.getDefault().getPathMatcher("glob:" + rule + "/*");
			PathMatcher globFolderAnywhere = FileSystems.getDefault().getPathMatcher("glob:" + "**/" + rule + "");
			if (glob.matches(path) || globAnywhere.matches(path) || globFolder.matches(path)
					|| globFolderAnywhere.matches(path)) {
				return true;
			}
		}

		return false;
	}

}
