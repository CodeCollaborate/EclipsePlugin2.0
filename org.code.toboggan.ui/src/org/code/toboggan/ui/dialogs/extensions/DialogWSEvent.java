package org.code.toboggan.ui.dialogs.extensions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.network.request.extensionpoints.websocket.IWSEvent;
import org.code.toboggan.ui.UIActivator;
import org.code.toboggan.ui.dialogs.WelcomeDialog;
import org.code.toboggan.ui.preferences.PreferenceConstants;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DialogWSEvent implements IWSEvent {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Override
	public void onConnect() {
		logger.debug("UI-DEBUG: Websocket connected, attempting to login");
		
		ISecurePreferences secureStore = SecurePreferencesFactory.getDefault();
		final String[] username = {null};
		final String[] password = {null};
		try {
			username[0] = secureStore.get(PreferenceConstants.USERNAME, null);
			password[0] = secureStore.get(PreferenceConstants.PASSWORD, null);
		} catch (StorageException e) {
			e.printStackTrace();
		}
		IPreferenceStore prefStore = UIActivator.getDefault().getPreferenceStore();

		if (username[0] == null || username[0].equals("") || password[0] == null || password[0].equals("")) {
			logger.debug("UI-DEBUG: Username or password not found, opening welcome dialog");
			Display.getDefault().asyncExec(() -> {
				Shell shell = Display.getDefault().getActiveShell();
				new WelcomeDialog(shell, secureStore).open();
			});
		} else {
			logger.debug("UI-DEBUG: Connecting");
			// TODO: Re-enable this check once there is a way to manually connect
			// if (prefStore.getBoolean(PreferenceConstants.AUTO_CONNECT)) {
			APIFactory.createUserLogin(username[0], password[0]).runAsync();
			// }
		}
	}

	@Override
	public void onClose() {
		// do nothing
	}

	@Override
	public void onError() {
		// do nothing
	}

}
