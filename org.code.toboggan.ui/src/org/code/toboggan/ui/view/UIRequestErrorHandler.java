package org.code.toboggan.ui.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import clientcore.websocket.IRequestSendErrorHandler;

public class UIRequestErrorHandler implements IRequestSendErrorHandler {
	private Logger logger = LogManager.getLogger(this.getClass());

	private String errorMsg;
	
	public UIRequestErrorHandler(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	@Override
	public void handleRequestSendError() {
		logger.debug("UI-DEBUG: Request failed to send - UIRequestErrorHandler");
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog(errorMsg).open());
	}
}
