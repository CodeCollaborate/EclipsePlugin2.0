package org.code.toboggan.ui.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.code.toboggan.ui.UIActivator;
import org.code.toboggan.ui.dialogs.AddProjectDialog;
import org.code.toboggan.ui.dialogs.DeleteProjectDialog;
import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.Project;

public class ProjectsListView extends ListView {

	public ProjectsListView(Composite parent, int style) {
		super(parent, style, "Projects");
		this.initializeData();
		this.initContextMenu();
		this.initButtonListeners();
	}

	private void initContextMenu() {
		HListWithVButtons lwb = this.getListWithButtons();
		List list = lwb.getList();
		Menu menu = new Menu(list);
		list.setMenu(menu);
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				int selected = list.getSelectionIndex();

				for (MenuItem item : menu.getItems()) {
					item.dispose();
				}

				if (selected < 0 || selected > list.getItemCount()) {
					return;
				}

				Project selectedProj = null;
				java.util.List<Project> projects = UIActivator.getDefault().getSessionStorage().getSortedProjects();
				selectedProj = projects.get(selected);

				boolean subscribed = UIActivator.getDefault().getSessionStorage()
						.getSubscribedIds().contains(selectedProj.getProjectID());
				if (subscribed) {
					ProjectListMenuItemFactory.makeUnsubscribeItem(menu, selectedProj);
				} else {
					ProjectListMenuItemFactory.makeSubscribeItem(menu, selectedProj);
				}
			}
		});
		list.addListener(SWT.Selection, (event) -> {
			if (list.getSelectionIndices().length != 0) {
				lwb.getButtonBar().getMinusButton().setEnabled(true);
			}
		});
	}

	PropertyChangeListener projectListListener;
	private void initializeData() {
		// register handler for projects
		List list = getListWithButtons().getList();
		projectListListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getPropertyName() != SessionStorage.PROJECT_LIST) {
					return;
				}
				Display.getDefault().asyncExec(() -> {
					if (!list.isDisposed()) {
						list.removeAll();
					}
					for (Project p : UIActivator.getDefault().getSessionStorage().getSortedProjects()) {
						if (!list.isDisposed()) {
							list.add(p.getName());
						}
					}
				});
			}
		};
		UIActivator.getDefault().getSessionStorage().addPropertyChangeListener(projectListListener);
	}

	private void removePropertyListeners() {
		UIActivator.getDefault().getSessionStorage().removePropertyChangeListener(projectListListener);
	}

	private void initButtonListeners() {
		List list = getListWithButtons().getList();
		VerticalButtonBar bar = this.getListWithButtons().getButtonBar();

		// plus button
		bar.getPlusButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				Shell shell = Display.getDefault().getActiveShell();
				AddProjectDialog dialog = new AddProjectDialog(shell);
				getShell().getDisplay().asyncExec(()-> dialog.open());
			}
		});

		// minus button
		bar.getMinusButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				if (list.getSelectionIndex() == -1) {
					MessageDialog.createDialog("No project is selected.").open();
					return;
				}

				java.util.List<Project> projects = UIActivator.getDefault().getSessionStorage().getSortedProjects();
				Project selectedProject = projects.get(list.getSelectionIndex());

				Shell shell = Display.getDefault().getActiveShell();
				DeleteProjectDialog delete = new DeleteProjectDialog(shell, selectedProject);
				delete.open();
			}
		});

		// reload button
		bar.getReloadButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
//				PluginManager.getInstance().getRequestManager().fetchProjects();
			}

		});
	}

	public void initSelectionListener(Listener listener) {
		List list = this.getListWithButtons().getList();
		list.addListener(SWT.Selection, listener);
	}

	@Override
	public void dispose() {
		removePropertyListeners();
		super.dispose();
	}
}
