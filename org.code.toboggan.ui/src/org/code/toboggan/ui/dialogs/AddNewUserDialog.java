package org.code.toboggan.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.ui.UIActivator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.google.common.collect.BiMap;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.Permission;
import clientcore.websocket.models.Project;

public class AddNewUserDialog extends Dialog {
	private Logger logger = LogManager.getLogger(this.getClass());

	private CCombo combo;
	private Label errorLabel;
	private int permission;
	private Button okButton;
	private Project selectedProject;
	private BiMap<String, Integer> permissionMap;
	private Text usernamesBox;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public AddNewUserDialog(Shell parentShell, Project selectedProject) {
		super(parentShell);
		this.selectedProject = selectedProject;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		logger.debug("UI-DEBUG: Creating AddNewUserDialog");

		Composite container = (Composite) super.createDialogArea(parent);
		Label lblAddANew = new Label(container, SWT.NONE);
		lblAddANew.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblAddANew.setText(DialogStrings.AddNewUserDialog_AddByUsername);

		usernamesBox = new Text(container, SWT.BORDER);
		GridData gd_text = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		usernamesBox.setLayoutData(gd_text);

		combo = new CCombo(container, SWT.BORDER);
		combo.setEditable(false);
		combo.setText(DialogStrings.AddNewUserDialog_ChoosePermission);
		GridData gd_combo = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		combo.setLayoutData(gd_combo);

		SessionStorage ss = UIActivator.getSessionStorage();
		permissionMap = ss.getPermissionConstants();
		BiMap<Integer, String> inversePermissionMap = permissionMap.inverse();
		List<Integer> permissionCodes = new ArrayList<>(permissionMap.values());
		Map<String, Permission> userPermissions = selectedProject.getPermissions();
		int userLevel = userPermissions.get(ss.getUsername()).getPermissionLevel();
		Collections.sort(permissionCodes);
		for (Integer perm : permissionCodes) {
			if (userLevel > perm) {
				combo.add(perm + " : " + inversePermissionMap.get(perm));
			}
		}
		final boolean[] permissionSelected = { false };
		final boolean[] usernameNotEmpty = { false };
		combo.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				permissionSelected[0] = true;
				if (permissionSelected[0] && usernameNotEmpty[0]) {
					okButton.setEnabled(true);
				} else {
					okButton.setEnabled(false);
				}
			}
		});

		usernamesBox.addModifyListener((event) -> {
			if (usernamesBox.getText() != "") {
				usernameNotEmpty[0] = true;
			} else {
				usernameNotEmpty[0] = false;
			}
			if (permissionSelected[0] && usernameNotEmpty[0]) {
				okButton.setEnabled(true);
			} else {
				okButton.setEnabled(false);
			}
		});

		errorLabel = new Label(container, SWT.NONE);
		errorLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		GridData gd_errorLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		errorLabel.setLayoutData(gd_errorLabel);
		errorLabel.setText(DialogStrings.AddNewUserDialog_ErrLabelPlaceholder);
		errorLabel.setVisible(false);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		okButton.setText(DialogStrings.AddNewUserDialog_AddButton);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		logger.debug("UI-DEBUG: Ok button for AddNewUserDialog was pressed");
		String usernamesStr = usernamesBox.getText();
		permission = Integer.parseInt(combo.getItem(combo.getSelectionIndex()).split(" . ")[0]);
		if (usernamesStr != null && permission != -1) {
			String[] usernames = usernamesStr.split(",");

			for (String username : usernames) {
				username = username.trim();
				
				// Skip empty usernames
				if (username.isEmpty()){
					continue;
				}
				
				if (username.equals(CoreActivator.getSessionStorage().getUsername())) {
					MessageDialog.createDialog(DialogStrings.ProjectSettingsDialog_GrantPermissionCurrUser).open();
				} else {
					APIFactory.createProjectGrantPermissions(selectedProject.getProjectID(), username, permission)
							.runAsync();
					super.okPressed();
				}
			}
		}
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(DialogStrings.AddNewUserDialog_Title);
	}
}
