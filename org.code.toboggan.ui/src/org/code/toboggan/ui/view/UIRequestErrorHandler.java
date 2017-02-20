package org.code.toboggan.ui.view;

import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import clientcore.websocket.IRequestSendErrorHandler;

public class UIRequestErrorHandler implements IRequestSendErrorHandler {

	private String errorMsg;
	
	public UIRequestErrorHandler(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	@Override
	public void handleRequestSendError() {
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog(errorMsg).open());
	}
}
