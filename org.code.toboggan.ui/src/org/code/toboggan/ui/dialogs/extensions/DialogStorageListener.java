package org.code.toboggan.ui.dialogs.extensions;

import java.beans.PropertyChangeEvent;

import org.code.toboggan.core.extensionpoints.storage.IStorageListener;
import org.code.toboggan.ui.dialogs.ConfirmSubscribeDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import clientcore.dataMgmt.SessionStorage;

public class DialogStorageListener implements IStorageListener {

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Only check username; tokens can be updated without breaking the
		// session
		if (evt.getPropertyName().equals(SessionStorage.USERNAME)) {
			Display.getDefault().asyncExec(() -> {
				Shell shell = Display.getDefault().getActiveShell();
				new ConfirmSubscribeDialog(shell).open();
			});
		}
	}

}
