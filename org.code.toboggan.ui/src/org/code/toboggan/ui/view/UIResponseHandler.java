package org.code.toboggan.ui.view;

import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import clientcore.websocket.IResponseHandler;
import clientcore.websocket.models.Response;

public class UIResponseHandler implements IResponseHandler {

	private String requestName;
	
	public UIResponseHandler(String requestName) {
		this.requestName = requestName;
	}
	
	@Override
	public void handleResponse(Response response) {
		if (response.getStatus() != 200) {
			Display.getDefault().asyncExec(() -> MessageDialog.createDialog(requestName + " failed with status code " + response.getStatus() + ".").open());
		}
	}
}
