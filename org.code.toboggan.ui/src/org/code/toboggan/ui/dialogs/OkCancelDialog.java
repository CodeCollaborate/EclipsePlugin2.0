package org.code.toboggan.ui.dialogs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class OkCancelDialog extends Dialog {
	private static Logger logger = LogManager.getLogger(OkCancelDialog.class);

	private String message;
	private String okText = IDialogConstants.OK_LABEL;
	private String cancelText = IDialogConstants.CANCEL_LABEL;
	private boolean swapDefaults = false;

	protected OkCancelDialog(Shell parentShell) {
		super(parentShell);
	}

	protected OkCancelDialog(Shell parentShell, String msg) {
		super(parentShell);
		message = msg;
	}

	protected OkCancelDialog(Shell parentShell, String msg, String okText, String cancelText, boolean swapDefaults) {
		super(parentShell);
		message = msg;
		this.okText = okText;
		this.cancelText = cancelText;
		this.swapDefaults = swapDefaults;
	}

	public static OkCancelDialog createDialog(String msg) {
		logger.debug("UI-DEBUG: Building new OkCancelDialog");
		
		Shell shell = Display.getDefault().getActiveShell();
		OkCancelDialog dialog = new OkCancelDialog(shell, msg);
		return dialog;
	}

	public static OkCancelDialog createDialog(String msg, String okText, String cancelText, boolean swapDefaults) {
		logger.debug("UI-DEBUG: Building new OkCancelDialog with different ok, cancel texts");

		Shell shell = Display.getDefault().getActiveShell();
		OkCancelDialog dialog = new OkCancelDialog(shell, msg, okText, cancelText, swapDefaults);
		return dialog;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, okText, !swapDefaults);
		createButton(parent, IDialogConstants.CANCEL_ID, cancelText, swapDefaults);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		logger.debug("UI-DEBUG: Creating OkCancelDialog");
		Composite container = (Composite) super.createDialogArea(parent);
		container.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));

		Label label = new Label(container, SWT.WRAP | SWT.CENTER);
		GridData gd_label = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		label.setLayoutData(gd_label);
		label.setText(message);

		return container;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("CodeCollaborate");
	}
}
