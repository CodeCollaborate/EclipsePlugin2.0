package org.code.toboggan.ui.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class ListViewsParent extends Composite {
	private Logger logger = LogManager.getLogger(this.getClass());
	
	private ProjectsListView projectListView;
	private UsersListView userListView;

	public ListViewsParent(Composite parent, int style) {
		super(parent, style);
		this.initialize();
	}
	
	private void initialize() {
		FormLayout formLayout = new FormLayout();
		this.setLayout(formLayout);
		this.projectListView = this.createProjectsList();
		this.userListView = this.createUsersList(this.projectListView);
	}
	
	private ProjectsListView createProjectsList() {
		logger.debug("UI-DEBUG: Creating Projects list");
		ProjectsListView listView = new ProjectsListView(this, SWT.BORDER);
		FormData data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(40);
		data.bottom = new FormAttachment(100);
		listView.setLayoutData(data);
		return listView;
	}
	
	private UsersListView createUsersList(ProjectsListView projectsListView) {
		logger.debug("UI-DEBUG: Creating Users List");
		UsersListView listView = new UsersListView(this, SWT.BORDER, projectListView);
		FormData data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(projectListView);
		data.right = new FormAttachment(100);
		data.bottom = new FormAttachment(100);
		listView.setLayoutData(data);
		return listView;
	}
	
	public ProjectsListView getProjectListView() {
		return projectListView;
	}
	
	public UsersListView getUserListView() {
		return userListView;
	}
}
