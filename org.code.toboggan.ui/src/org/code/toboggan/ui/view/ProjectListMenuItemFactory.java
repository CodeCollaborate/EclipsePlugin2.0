package org.code.toboggan.ui.view;

import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.ui.dialogs.OkCancelDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import clientcore.websocket.models.Project;

public class ProjectListMenuItemFactory {
	
	public static void makeSubscribeItem(Menu parentMenu, Project p) {
		MenuItem sub = new MenuItem(parentMenu, SWT.NONE);
		sub.setText("Subscribe");
		sub.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				Display.getDefault().asyncExec(() -> {
					if (Window.OK == OkCancelDialog.createDialog("Subscribing will overwrite all local changes with those that are on the server.").open()) {
						new Thread(APIFactory.createProjectSubscribe(p.getProjectID())).start();
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
				new Thread(APIFactory.createProjectUnsubscribe(p.getProjectID())).start();
			}
		});
	}
	
}
