package org.code.toboggan.filesystem.editor;

import java.util.HashMap;
import java.util.Map;

public class ShadowDocumentManager {
	private final Map<Long, String> documentShadows;

	public ShadowDocumentManager() {
		this.documentShadows = new HashMap<>();
	}

	public synchronized String getShadow(long fileID) {
		return this.documentShadows.get(fileID);
	}

	public synchronized String putShadow(long fileID, String contents) {
		return this.documentShadows.put(fileID, contents);
	}
}
