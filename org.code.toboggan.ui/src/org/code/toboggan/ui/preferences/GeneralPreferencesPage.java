package org.code.toboggan.ui.preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.ui.UIActivator;
import org.code.toboggan.ui.dialogs.RecoverPasswordDialog;
import org.code.toboggan.ui.dialogs.WelcomeDialog;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import clientcore.websocket.ConnectException;
import constants.PreferenceConstants;

public class GeneralPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private Logger logger = LogManager.getLogger(this.getClass());

	public GeneralPreferencesPage() {
		super(GRID);
		setPreferenceStore(UIActivator.getDefault().getPreferenceStore());
		setDescription("CodeCollaborate preferences");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		Button loginButton = new Button(getFieldEditorParent(), SWT.PUSH);
		loginButton.setText("Login to CodeCollaborate");
		loginButton.addListener(SWT.Selection, (event) -> {
			Display.getDefault().asyncExec(() -> {
				logger.debug("UI-DEBUG: Preferences login to codecollaborate button pressed");
				Shell shell = Display.getDefault().getActiveShell();
				ISecurePreferences secureStore = SecurePreferencesFactory.getDefault();
				new WelcomeDialog(shell, secureStore).open();
			});
		});

		BooleanFieldEditor autoConnect = new BooleanFieldEditor(PreferenceConstants.AUTO_CONNECT,
				"Auto-connect on startup", getFieldEditorParent());
		// change when implementing other ways to connect other than on startup
		autoConnect.setEnabled(false, getFieldEditorParent());

		Button reconnect = new Button(getFieldEditorParent(), SWT.PUSH);
		reconnect.setText("Reconnect to Server");
		reconnect.addListener(SWT.Selection, (event) -> {
			new Thread(() -> {
				try {
					logger.debug("UI-DEBUG: Preferences Reconnect to server button pressed");
					UIActivator.getWSManager().connect();
				} catch (ConnectException e) {
					e.printStackTrace();
				}
			}).start();
		});

		Button forgotPassword = new Button(getFieldEditorParent(), SWT.PUSH);
		forgotPassword.setText("Forgotten Password?");
		forgotPassword.addListener(SWT.Selection, (event) -> {
			new Thread(() -> {
				logger.debug("UI-DEBUG: Preferences forgot password button pressed");
				Display.getDefault().asyncExec(() -> new RecoverPasswordDialog(getShell()).open());
			}).start();
		});
		addField(autoConnect);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		// do nothing
	}

}