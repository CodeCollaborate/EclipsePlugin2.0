package org.code.toboggan.ui.view;

import java.beans.PropertyChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.network.NetworkActivator;
import org.code.toboggan.ui.UIActivator;
import org.code.toboggan.ui.dialogs.DialogStrings;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.ConnectException;
import clientcore.websocket.WSConnection;
import clientcore.websocket.WSConnection.State;
import clientcore.websocket.WSManager;

public class ControlPanel extends ViewPart {
	private final Logger logger = LogManager.getLogger(ControlPanel.class);

	static ControlPanel instance;

	public static ControlPanel getInstance() {
		return instance;
	}

	public ControlPanel() {
		instance = this;

		// start after control panel is instantiated.
		new Thread(() -> {
			try {
				UIActivator.getWSManager().connect();
			} catch (ConnectException e) {
				e.printStackTrace();
			}
		}).start();
	}

	protected ListViewer projectsListViewer;
	protected ListViewer usersListViewer;
	private StatusBar statusBar;
	private ListViewsParent views;

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		parent.setLayout(layout);

		views = new ListViewsParent(parent, SWT.NONE);

		GridData viewsData = new GridData();
		viewsData.grabExcessHorizontalSpace = true;
		viewsData.horizontalAlignment = GridData.FILL;
		viewsData.grabExcessVerticalSpace = true;
		viewsData.verticalAlignment = GridData.FILL;
		views.setLayoutData(viewsData);

		statusBar = new StatusBar(parent, SWT.BORDER);
		GridData statusData = new GridData();
		statusData.grabExcessHorizontalSpace = true;
		statusData.horizontalAlignment = GridData.FILL;
		statusBar.setLayoutData(statusData);
		initializePropertyChangeListeners();
		initializeStatusBar();

		if (UIActivator.getSessionStorage().getUsername() != null) {
			setEnabled(true);
			APIFactory.createUserProjects().runAsync();
		}
	}

	private PropertyChangeListener usernameListener;

	private void initializePropertyChangeListeners() {
		usernameListener = (event) -> {
			if (!event.getPropertyName().equals(SessionStorage.USERNAME)) {
				return;
			}

			if (event.getNewValue() != null) {
				Display.getDefault().asyncExec(() -> this.setEnabled(true));
			} else {
				Display.getDefault().asyncExec(() -> this.setEnabled(false));
			}
		};

		UIActivator.getSessionStorage().addPropertyChangeListener(usernameListener);
	}

	private void initializeStatusBar() {
		WSManager wsManager = UIActivator.getWSManager();
		State s = wsManager.getConnectionState();
		logger.debug(String.format("STATE: [%s]", s.toString()));
		switch (s) {
		case CREATED:
			statusBar.setStatus(DialogStrings.StartingUp_Message);
			break;
		case CONNECT:
			statusBar.setStatus(DialogStrings.Connecting_Message);
			break;
		case READY:
			statusBar.setStatus(DialogStrings.Connected_Message);
			break;
		case CLOSE:
			statusBar.setStatus(DialogStrings.Disconnecting_Message);
			break;
		case EXIT:
			statusBar.setStatus(DialogStrings.Disconnected_Message);
			break;
		case ERROR:
			statusBar.setStatus(DialogStrings.Error_Connecting_Message);
			break;
		default:
			statusBar.setStatus(DialogStrings.Error_Connecting_Message);
			break;
		}
	}

	private void undoNotificationHandlers() {
		WSManager wsManager = NetworkActivator.getWSService().getWSManager();
		wsManager.deregisterEventHandler(WSConnection.EventType.ON_CONNECT);
		wsManager.deregisterEventHandler(WSConnection.EventType.ON_CLOSE);
		wsManager.deregisterEventHandler(WSConnection.EventType.ON_ERROR);
	}

	private void removePropertyChangeListener() {
		UIActivator.getSessionStorage().removePropertyChangeListener(usernameListener);
	}

	@Override
	public void setFocus() {
		// do nothing
	}

	public void setEnabled(boolean b) {
		views.getProjectListView().getListWithButtons().setEnabled(b);
		views.getUserListView().getListWithButtons().setEnabled(b);
	}

	@Override
	public void dispose() {
		undoNotificationHandlers();
		removePropertyChangeListener();
		super.dispose();
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

}