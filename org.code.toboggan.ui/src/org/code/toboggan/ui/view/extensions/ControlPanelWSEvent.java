package org.code.toboggan.ui.view.extensions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.network.request.extensionpoints.websocket.IWSEvent;
import org.code.toboggan.ui.dialogs.DialogStrings;
import org.code.toboggan.ui.view.ControlPanel;

public class ControlPanelWSEvent implements IWSEvent {
	Logger logger = LogManager.getLogger(ControlPanelWSEvent.class);
	private ControlPanel instance;
	
	public ControlPanelWSEvent() {
		instance = ControlPanel.getInstance();
	}
	
	public void checkInstance() {
		if (instance == null) {
			// try to get instance again
			instance = ControlPanel.getInstance();
		}
	}
	
	@Override
	public void onConnect() {
		logger.debug("UI-DEBUG: Websocket state is now Connected");
		checkInstance();
		
		if (instance != null) {
			instance.getStatusBar().getDisplay().asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.Connected_Message));
		} else {
			logger.debug("Instance of control panel was null");
		}
	}

	@Override
	public void onClose() {
		logger.debug("UI-DEBUG: Websocket state is now Closed");
		checkInstance();
		
		if (instance != null) {
			instance.getStatusBar().getDisplay().asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.Disconnected_Message));
		} else {
			logger.debug("Instance of control panel was null");
		}
	}

	@Override
	public void onError() {
		logger.debug("UI-DEBUG: Websocket state is now Errored");
		checkInstance();
		
		if (instance != null) {
			instance.getStatusBar().getDisplay().asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.Error_Connecting_Message));
		} else {
			logger.debug("Instance of control panel was null");
		}
	}

}
