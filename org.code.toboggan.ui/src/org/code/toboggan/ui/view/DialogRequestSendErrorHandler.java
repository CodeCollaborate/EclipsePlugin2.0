package org.code.toboggan.ui.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import clientcore.websocket.IRequestSendErrorHandler;

public class DialogRequestSendErrorHandler implements IRequestSendErrorHandler {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Override
	public void handleRequestSendError() {
		logger.debug("UI-DEBUG: A request failed to send - DialogRequestSendErrorHandler");
		// TODO: Log more detailed message
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog("Could not send request.").open());
	}
}
