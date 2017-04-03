package org.code.toboggan.ui.view.extensions;

import java.beans.PropertyChangeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.storage.IStorageListener;
import org.code.toboggan.network.request.extensionpoints.websocket.IWSEvent;
import org.code.toboggan.ui.UIActivator;
import org.code.toboggan.ui.dialogs.DialogStrings;
import org.code.toboggan.ui.view.ControlPanel;

import clientcore.websocket.WSConnection.State;
import clientcore.websocket.WSManager;

public class ControlPanelWSEvent implements IWSEvent, IStorageListener {
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
	public void propertyChange(PropertyChangeEvent evt) {
		updateStatus();
	}

	@Override
	public void onConnect() {
		updateStatus();
	}

	@Override
	public void onClose() {
		updateStatus();
	}

	@Override
	public void onError() {
		updateStatus();
	}

	public void updateStatus() {
		checkInstance();
		
		if (instance != null) {
			WSManager wsManager = UIActivator.getWSManager();
			State s = wsManager.getConnectionState();
			String username = UIActivator.getSessionStorage().getUsername();

			logger.debug(String.format("Websocket status is now [%s], logged in [%b]", s.toString(), username != null));
			switch (s) {
			case CREATED:
				instance.getStatusBar().getDisplay()
						.asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.StartingUp_Message));
				break;
			case CONNECT:
				instance.getStatusBar().getDisplay()
						.asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.Connecting_Message));
				break;
			case READY:
				if (username != null) {
					instance.getStatusBar().getDisplay()
					.asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.Connected_LoggedIn_Message));
				} else {
					instance.getStatusBar().getDisplay()
							.asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.Connected_Message));
				}
				break;
			case CLOSE:
				instance.getStatusBar().getDisplay()
						.asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.Disconnecting_Message));
				break;
			case EXIT:
				instance.getStatusBar().getDisplay()
						.asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.Disconnected_Message));
				break;
			case ERROR:
				instance.getStatusBar().getDisplay()
						.asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.Error_Connecting_Message));
				break;
			default:
				instance.getStatusBar().getDisplay()
						.asyncExec(() -> instance.getStatusBar().setStatus(DialogStrings.Error_Connecting_Message));
				break;
			}
		} else {
			logger.debug("Instance of control panel was null");
		}
	}
}
