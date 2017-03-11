package org.code.toboggan.ui.dialogs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class RemoveUserDialog extends Dialog {
	private Logger logger = LogManager.getLogger(this.getClass());

	private String username;
	private String projectName;
	private long projectId;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public RemoveUserDialog(Shell parentShell) {
		super(parentShell);
	}

	public RemoveUserDialog(Shell parentShell, String username, String projectName, long projectId) {
		super(parentShell);
		this.username = username;
		this.projectName = projectName;
		this.projectId = projectId;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		logger.debug("UI-DEBUG: Creating RemoveUserDialog");
		Composite container = (Composite) super.createDialogArea(parent);

		Label lblAreYouSure = new Label(container, SWT.WRAP | SWT.CENTER);
		GridData gd_lblAreYouSure = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		lblAreYouSure.setLayoutData(gd_lblAreYouSure);
		lblAreYouSure.setText(DialogStrings.RemoveUserDialog_AreYouSure1 + username + DialogStrings.RemoveUserDialog_AreYouSure2 + projectName + DialogStrings.RemoveUserDialog_AreYouSure3);

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
		button.setText(DialogStrings.RemoveUserDialog_RemoveButton);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	@Override
	public void okPressed() {
		logger.debug("UI-DEBUG: Ok button pressed for RemoveUserDialog");
		APIFactory.createRevokePermissions(projectId, username).runAsync();
		super.okPressed();
	}
	
	@Override
	protected void configureShell(Shell shell) {
	      super.configureShell(shell);
	      shell.setText(DialogStrings.RemoveUserDialog_Title);
	}

}
