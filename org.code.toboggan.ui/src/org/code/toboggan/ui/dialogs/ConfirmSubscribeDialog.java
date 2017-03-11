package org.code.toboggan.ui.dialogs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.ui.preferences.SubscribedPreferencesController;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ConfirmSubscribeDialog extends OkCancelDialog {
	private static Logger logger = LogManager.getLogger(ConfirmSubscribeDialog.class);

	protected ConfirmSubscribeDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	protected ConfirmSubscribeDialog(Shell parentShell, String message) {
		super(parentShell, message);
	}
	
	public static ConfirmSubscribeDialog createDialog(String msg) {
		logger.debug("UI-DEBUG: Creating dialog for ConfirmSubscribeDialog");
        final ConfirmSubscribeDialog[] dialog = new ConfirmSubscribeDialog[1];
        Display.getDefault().syncExec(() -> {
            Shell shell = Display.getDefault().getActiveShell();
            dialog[0] = new ConfirmSubscribeDialog(shell, msg);
        });
        return dialog[0];
	}
	
	@Override
	public void okPressed() {
		logger.debug("UI-DEBUG: Ok button pressed for ConfirmSubscribeDialog");
		APIFactory.createProjectFetchAndSubscribeAll(SubscribedPreferencesController.getSubscribedProjectIds()).runAsync();
	}
}
