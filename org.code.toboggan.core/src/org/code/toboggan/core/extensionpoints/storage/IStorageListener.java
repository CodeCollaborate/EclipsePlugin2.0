package org.code.toboggan.core.extensionpoints.storage;

import java.beans.PropertyChangeEvent;

import org.code.toboggan.core.extensionpoints.ICoreExtension;

public interface IStorageListener extends ICoreExtension {
	public void propertyChange(PropertyChangeEvent evt);
}
