package org.code.toboggan.ui.view;

import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import clientcore.requestMgmt.IInvalidResponseHandler;

public class DialogInvalidResponseHandler implements IInvalidResponseHandler {

	@Override
	public void handleInvalidResponse(int errorCode, String message) {
		// TODO: Make more specific for different error codes
		Display.getDefault().asyncExec(() -> MessageDialog.createDialog(message).open());
	}

}
