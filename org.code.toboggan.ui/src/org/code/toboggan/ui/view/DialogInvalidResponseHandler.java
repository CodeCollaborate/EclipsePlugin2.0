package org.code.toboggan.ui.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import clientcore.requestMgmt.IInvalidResponseHandler;

public class DialogInvalidResponseHandler implements IInvalidResponseHandler {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Override
	public void handleInvalidResponse(int errorCode, String message) {
		logger.debug(
				"UI-DEBUG: Error response received - DialogInvalidResponseHandler: " + errorCode + " - " + message);
		// TODO: Make more specific for different error codes
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog(message).open());
	}

}
