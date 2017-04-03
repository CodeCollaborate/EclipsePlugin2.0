package org.code.toboggan.ui.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
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
import clientcore.websocket.models.Permission;
import clientcore.websocket.models.Project;

public class ProjectsListView extends ListView {
	private Logger logger = LogManager.getLogger(this.getClass());

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
				logger.debug("UI-DEBUG: MenuListener was notified that the context menu was triggered");
				int selected = list.getSelectionIndex();

				for (MenuItem item : menu.getItems()) {
					item.dispose();
				}

				if (selected < 0 || selected > list.getItemCount()) {
					return;
				}

				Project selectedProj = null;
				java.util.List<Project> projects = UIActivator.getSessionStorage().getSortedProjects();
				selectedProj = projects.get(selected);

				boolean subscribed = UIActivator.getSessionStorage().getSubscribedIds()
						.contains(selectedProj.getProjectID());
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
				logger.debug(
						"UI-DEBUG: ProjectListListener was notified of a change in the sessionStorage projectList, for event "
								+ event.toString());
				if (event.getPropertyName() != SessionStorage.PROJECT_LIST) {
					return;
				}
				Display.getDefault().asyncExec(() -> {
					if (!list.isDisposed()) {
						list.removeAll();
					}
					for (Project p : UIActivator.getSessionStorage().getSortedProjects()) {
						if (!list.isDisposed()) {
							Permission owner = null;
							for (Permission perm : p.getPermissions().values()) {
								if (perm.getPermissionLevel() == UIActivator.getSessionStorage()
										.getPermissionConstants().get("owner")) {
									owner = perm;
								}
							}

							if (owner != null) {
								list.add(String.format("%s/%s", owner.getUsername(), p.getName()));
							} else {
								logger.error(String.format("Could not find owner for project [%d]: [%s]",
										p.getProjectID(), p.getName()));
							}
						}
					}
				});
			}
		};
		UIActivator.getSessionStorage().addPropertyChangeListener(projectListListener);
	}

	private void removePropertyListeners() {
		UIActivator.getSessionStorage().removePropertyChangeListener(projectListListener);
	}

	private void initButtonListeners() {
		List list = getListWithButtons().getList();
		VerticalButtonBar bar = this.getListWithButtons().getButtonBar();

		// plus button
		bar.getPlusButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				logger.debug("UI-DEBUG: Projects list plus button pressed");
				Shell shell = Display.getDefault().getActiveShell();
				AddProjectDialog dialog = new AddProjectDialog(shell);
				getShell().getDisplay().asyncExec(() -> dialog.open());
			}
		});

		// minus button
		bar.getMinusButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				logger.debug("UI-DEBUG: Projects list minus button pressed");
				if (list.getSelectionIndex() == -1) {
					MessageDialog.createDialog("No project is selected.").open();
					return;
				}

				java.util.List<Project> projects = UIActivator.getSessionStorage().getSortedProjects();
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
				logger.debug("UI-DEBUG: Projects list reload button pressed");

				APIFactory.createUserProjects().runAsync();
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
