package org.code.toboggan.ui.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.api.APIFactory;
import org.code.toboggan.ui.UIActivator;
import org.code.toboggan.ui.dialogs.MessageDialog;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import clientcore.dataMgmt.SessionStorage;
import clientcore.websocket.models.Project;

public class SubscribedPreferencesController {
	private static final Logger logger = LogManager.getLogger("");
	
	private static void autoSubscribe() {
		SessionStorage storage = UIActivator.getDefault().getSessionStorage();
		List<Long> subscribedIdsFromPrefs = getSubscribedProjectIds();
		Set<Long> subscribedIds = storage.getSubscribedIds();

		for (Long id : subscribedIdsFromPrefs) {
			Project p = storage.getProject(id);
			if (p == null) {
				removeProjectIdFromPrefs(id);
			} else if (!subscribedIds.contains(id)) {
				new Thread(APIFactory.createProjectSubscribe(id)).start();
			}
		}
	}
	
	private static void removeProjectIdFromPrefs(long id) {
		logger.debug(String.format("Removing project %d from auto-subscribe prefs", id));
		Preferences pluginPrefs = InstanceScope.INSTANCE.getNode(UIActivator.PLUGIN_ID);
		Preferences projectPrefs = pluginPrefs.node(PreferenceConstants.NODE_PROJECTS);
		try {
			String sid = Long.toString(id);
			if (projectPrefs.nodeExists(sid)) {
				Preferences thisProjectPrefs = projectPrefs.node(sid);
				thisProjectPrefs.removeNode();
				logger.debug(String.format("Node removed for %s", sid));
			}
		} catch (BackingStoreException e) {
			MessageDialog.createDialog("Could not remove project from subscribe preferences.").open();
			e.printStackTrace();
		}
	}
	
	public static List<Long> getSubscribedProjectIds() {
		List<Long> subscribedProjectIds = new ArrayList<>();
		Preferences pluginPrefs = InstanceScope.INSTANCE.getNode(UIActivator.PLUGIN_ID);
		Preferences projectPrefs = pluginPrefs.node(PreferenceConstants.NODE_PROJECTS);
		String[] projectIDs;
		try {
			projectIDs = projectPrefs.childrenNames();
			logger.debug(String.format("Found %d auto-subscribe preferences", projectIDs.length));
			for (int i = 0; i < projectIDs.length; i++) {
				logger.debug(String.format("Read subscribe pref for project %s", projectIDs[i]));
				subscribedProjectIds.add(Long.parseLong(projectIDs[i]));
			}
		} catch (BackingStoreException e) {
			MessageDialog.createDialog("Could not read subscribed projects from preferences.").open();
			e.printStackTrace();
		}
		return Collections.unmodifiableList(subscribedProjectIds);
	}
	
	private static void removeAllSubscribedPrefs(boolean b) {
		Preferences pluginPrefs = InstanceScope.INSTANCE.getNode(UIActivator.PLUGIN_ID);
		Preferences projectPrefs = pluginPrefs.node(PreferenceConstants.NODE_PROJECTS);
		String[] projectIDs;
		try {
			projectIDs = projectPrefs.childrenNames();
			for (int i = 0; i < projectIDs.length; i++) {
				Preferences thisProjectPrefs = projectPrefs.node(projectIDs[i]);
				thisProjectPrefs.removeNode();
			}
			pluginPrefs.flush();
		} catch (BackingStoreException e) {
			MessageDialog.createDialog("Could not write subscribe preferences.").open();
			e.printStackTrace();
		}
	}
	
	private static void writeSubscribedProjects() {
		logger.debug("Writing subscribed projects to auto-subscribe preferences...");
		SessionStorage ss = UIActivator.getDefault().getSessionStorage();
		Set<Long> subscribedIDs = ss.getSubscribedIds();
		List<Project> projects = ss.getProjects();
		Preferences pluginPrefs = InstanceScope.INSTANCE.getNode(UIActivator.PLUGIN_ID);
		Preferences projectPrefs = pluginPrefs.node(PreferenceConstants.NODE_PROJECTS);

		for (Project p : projects) {
			boolean subscribed = subscribedIDs.contains(p.getProjectID());

			if (!subscribed) {
				// remove it from nodes if not subscribed
				try {
					if (projectPrefs.nodeExists(p.getProjectID() + "")) {
						Preferences thisProjectPrefs = projectPrefs.node(p.getProjectID() + "");
						thisProjectPrefs.removeNode();
						logger.debug(String.format("Node removed for %d", p.getProjectID()));
					}
				} catch (BackingStoreException e) {
					e.printStackTrace();
				}
			} else {
				// otherwise, make node
				Preferences thisProjectPrefs = projectPrefs.node(p.getProjectID() + "");
				// have to put something in it, otherwise the node will be
				// dumped
				thisProjectPrefs.putBoolean(PreferenceConstants.VAR_SUBSCRIBED, true);
				logger.debug(String.format("Wrote subscribed pref for project %d", p.getProjectID()));
			}
		}
		try {
			pluginPrefs.flush();
		} catch (BackingStoreException e) {
			logger.error("Could not write subscribe preferences", e);
		}
	}
}
