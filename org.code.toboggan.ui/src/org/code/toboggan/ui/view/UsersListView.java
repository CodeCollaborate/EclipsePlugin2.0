package org.code.toboggan.ui.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.CoreActivator;
import org.code.toboggan.ui.UIActivator;
import org.code.toboggan.ui.dialogs.AddNewUserDialog;
import org.code.toboggan.ui.dialogs.RemoveUserDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.Permission;
import clientcore.websocket.models.Project;

public class UsersListView extends ListView {
	private Logger logger = LogManager.getLogger(this.getClass());
	private Project currentProject = null;

	public UsersListView(Composite parent, int style, ProjectsListView listView) {
		super(parent, style, "Users");
		this.initializeListeners(listView);
	}

	private Project getProjectAt(int index) {
		java.util.List<Project> projects = UIActivator.getSessionStorage().getSortedProjects();
		if (index < 0 || index >= projects.size()) {
			return null;
		}
		return projects.get(index);
	}

	private void refreshSelected(ProjectsListView listView) {
		Display.getDefault().asyncExec(() -> {
			int selectedListIndex = listView.getListWithButtons().getList().getSelectionIndex();
			Project proj = getProjectAt(selectedListIndex);
			if (proj != null && UIActivator.getSessionStorage().getSubscribedIds().contains(proj.getProjectID())) {
				setProject(proj);
			} else {
				getListWithButtons().getList().removeAll();
				String projName = proj == null ? "a project" : proj.getName();
				String message = "You must be subscribed to " + projName + " to view users.";
				getListWithButtons().getList().add(message);
				VerticalButtonBar bar = getListWithButtons().getButtonBar();
				bar.getPlusButton().setEnabled(false);
				bar.getMinusButton().setEnabled(false);
				bar.getReloadButton().setEnabled(false);
			}
		});
	}

	PropertyChangeListener projectListListener;
	PropertyChangeListener projectPermissionListListener;

	private void initializeListeners(ProjectsListView listView) {
		// project listview selection
		listView.initSelectionListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				refreshSelected(listView);
			}
		});

		// project list property change
		projectListListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getPropertyName().equals(SessionStorage.PROJECT_LIST)) {
					if (!listView.isDisposed()) {
						Display.getDefault().asyncExec(() -> {
							int selectedListIndex = listView.getListWithButtons().getList().getSelectionIndex();
							if (selectedListIndex != -1) {
								SessionStorage storage = (SessionStorage) event.getSource();
								java.util.List<Project> projects = storage.getSortedProjects();
								Project project = projects.get(selectedListIndex);
								setProject(project);
							} else {
								setProject(null);
							}
						});
					}
				} else if (event.getPropertyName().equals(SessionStorage.SUBSCRIBED_PROJECTS)) {
					refreshSelected(listView);
				}
			}
		};
		UIActivator.getSessionStorage().addPropertyChangeListener(projectListListener);

		projectPermissionListListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				logger.debug(
						"UI-DEBUG: ProjectListListener was notified of a change in the sessionStorage projectPermissionList, for event "
								+ event.toString());
				if (currentProject != null && event.getPropertyName().equals(CoreActivator.getSessionStorage()
						.getProjectPermissionListIdentifer(currentProject.getProjectID()))) {
					if (!listView.isDisposed()) {
						Display.getDefault().asyncExec(() -> {
							// Run the update permission list
							setProject(currentProject);
						});
					}
				} else if (event.getPropertyName().equals(SessionStorage.SUBSCRIBED_PROJECTS)) {
					refreshSelected(listView);
				}
			}
		};
		UIActivator.getSessionStorage().addPropertyChangeListener(projectPermissionListListener);

		// plus button pressed
		VerticalButtonBar bar = this.getListWithButtons().getButtonBar();
		bar.getPlusButton().addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				logger.debug("UI-DEBUG: UsersListView plus button pressed");
				Display.getDefault().asyncExec(() -> {
					Shell shell = Display.getDefault().getActiveShell();
					List projectList = listView.getListWithButtons().getList();
					if (projectList.getSelectionIndex() == -1) {
						return;
					}
					Project p = UIActivator.getSessionStorage().getSortedProjects()
							.get(projectList.getSelectionIndex());
					AddNewUserDialog addUserDialog = new AddNewUserDialog(shell, p);
					addUserDialog.open();
				});
			}
		});

		// minus button pressed
		bar.getMinusButton().addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				logger.debug("UI-DEBUG: UsersListView minus button pressed");
				if (currentProject == null) {
					return;
				}
				List list = getListWithButtons().getList();
				Shell shell = Display.getDefault().getActiveShell();

				String[] keyset = currentProject.getPermissions().keySet()
						.toArray(new String[currentProject.getPermissions().keySet().size()]);
				Arrays.sort(keyset);

				Dialog removeUserDialog = new RemoveUserDialog(shell, keyset[list.getSelectionIndex()],
						currentProject.getName(), currentProject.getProjectID());
				removeUserDialog.open();
			}
		});

		// reload button
		bar.getReloadButton().addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				logger.debug("UI-DEBUG: UsersListView reload button pressed");
				refreshSelected(listView);
			}

		});

		// user list selection
		List list = this.getListWithButtons().getList();
		list.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				getListWithButtons().getButtonBar().getMinusButton().setEnabled(true);
			}
		});
	}

	private void removePropertyChangeListeners() {
		UIActivator.getSessionStorage().removePropertyChangeListener(projectListListener);
	}

	public void setProject(Project project) {
		logger.debug("UI-DEBUG: UsersListView - New project was set, discarding old users list");
		this.currentProject = project;
		Display.getDefault().asyncExec(() -> {
			List list = this.getListWithButtons().getList();
			if (!list.isDisposed()) {
				list.removeAll();
			}
			if (project == null) {
				getListWithButtons().getButtonBar().getPlusButton().setEnabled(false);
				getListWithButtons().getButtonBar().getMinusButton().setEnabled(false);
			} else {
				Map<String, Permission> permissions = project.getPermissions();
				if (permissions != null) {
					String[] keyset = permissions.keySet().toArray(new String[permissions.keySet().size()]);
					Arrays.sort(keyset);
					for (String username : keyset) {
						if (!list.isDisposed()) {
							Permission p = permissions.get(username);
							String permissionString = CoreActivator.getSessionStorage().getPermissionConstants()
									.inverse().get(p.getPermissionLevel());
							list.add(String.format("%s: %s", permissionString, username));
						}
					}
				}
				VerticalButtonBar bar = getListWithButtons().getButtonBar();
				bar.getPlusButton().setEnabled(true);
				bar.getReloadButton().setEnabled(true);
			}
		});
	}

	@Override
	public void dispose() {
		removePropertyChangeListeners();
		super.dispose();
	}
}
