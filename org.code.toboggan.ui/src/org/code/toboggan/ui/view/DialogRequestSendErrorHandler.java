package org.code.toboggan.ui.view;

import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import clientcore.websocket.IRequestSendErrorHandler;

public class DialogRequestSendErrorHandler implements IRequestSendErrorHandler {

	@Override
	public void handleRequestSendError() {
		// TODO: Log more detailed message
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog("Could not send request.").open());
	}
}
