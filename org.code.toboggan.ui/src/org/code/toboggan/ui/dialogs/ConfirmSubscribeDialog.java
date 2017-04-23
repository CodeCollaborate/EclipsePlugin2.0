package org.code.toboggan.ui.dialogs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.ui.preferences.SubscribedPreferencesController;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ConfirmSubscribeDialog extends Dialog {
	private Logger logger = LogManager.getLogger(this.getClass());

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public ConfirmSubscribeDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		logger.debug("UI-DEBUG: Creating dialog for ConfirmSubscribeDialog");
		Composite container = (Composite) super.createDialogArea(parent);

		Label lblWarningThisAction = new Label(container, SWT.WRAP | SWT.CENTER);
		lblWarningThisAction.setText(DialogStrings.SubscribeAllDialog_Message);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.setText(DialogStrings.SubscribeAllDialog_ConfirmButton);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		logger.debug("UI-DEBUG: Ok button pressed for ConfirmSubscribeDialog");
		APIFactory.createProjectFetchAndSubscribeAll(SubscribedPreferencesController.getSubscribedProjectIds())
				.runAsync();
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		logger.debug("UI-DEBUG: Cancel button pressed for ConfirmSubscribeDialog");
		SubscribedPreferencesController.removeAllSubscribedPrefs();
		APIFactory.createUserProjects().runAsync();
		super.cancelPressed();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(DialogStrings.SubscribeAllDialog_Title);
	}

}
