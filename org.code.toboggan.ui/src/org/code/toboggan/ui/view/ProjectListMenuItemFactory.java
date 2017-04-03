package org.code.toboggan.ui.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.ui.dialogs.OkCancelDialog;
import org.code.toboggan.ui.preferences.SubscribedPreferencesController;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import clientcore.websocket.models.Project;

public class ProjectListMenuItemFactory {
	private static Logger logger = LogManager.getLogger(ProjectListMenuItemFactory.class);

	public static void makeSubscribeItem(Menu parentMenu, Project p) {
		MenuItem sub = new MenuItem(parentMenu, SWT.NONE);
		sub.setText("Subscribe");
		sub.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				logger.debug("UI-DEBUG: Subscribe context menu item selected");
				Display.getDefault().asyncExec(() -> {
					// TODO: Make this a dialog string.
					if (Window.OK == OkCancelDialog
							.createDialog(
									"Subscribing will overwrite all local changes with those that are on the server.")
							.open()) {
						APIFactory.createProjectSubscribe(p.getProjectID()).runAsync();
						new Thread(() -> {
							SubscribedPreferencesController.writeSubscribedProjects();
						}).start();
					}
				});

			}

		});
	}

	public static void makeUnsubscribeItem(Menu parentMenu, Project p) {
		MenuItem unsub = new MenuItem(parentMenu, SWT.NONE);
		unsub.setText("Unsubscribe");
		unsub.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				logger.debug("UI-DEBUG: Unsubscribe context menu item selected");
				APIFactory.createProjectUnsubscribe(p.getProjectID()).runAsync();
				new Thread(() -> {
					SubscribedPreferencesController.writeSubscribedProjects();
				}).start();
			}
		});
	}

}
