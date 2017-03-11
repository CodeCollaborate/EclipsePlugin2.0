package org.code.toboggan.ui.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import clientcore.websocket.IResponseHandler;
import clientcore.websocket.models.Response;

public class UIResponseHandler implements IResponseHandler {
	private Logger logger = LogManager.getLogger(this.getClass());

	private String requestName;
	
	public UIResponseHandler(String requestName) {
		this.requestName = requestName;
	}
	
	@Override
	public void handleResponse(Response response) {
		if (response.getStatus() != 200) {
			logger.debug("UI-DEBUG: UIResponseHandler found non-success response");
			Display.getDefault().asyncExec(() -> MessageDialog.createDialog(requestName + " failed with status code " + response.getStatus() + ".").open());
		}
	}
}
