package org.code.toboggan.network;

import clientcore.websocket.WSManager;
import clientcore.websocket.models.ConnectionConfig;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.code.toboggan.core.extensionpoints.ICoreExtension;
import org.code.toboggan.network.request.extensionpoints.NetworkExtensionIDs;
import org.code.toboggan.network.request.extensionpoints.websocket.IWSEvent;
import org.code.toboggan.network.request.extensions.NetworkExtensionManager;

import clientcore.websocket.WSConnection;

public class WSService {
	final private static String WS_ADDRESS = "wss://codecollaborate.obsessiveorange.com:8000/ws/";
	final private static boolean RECONNECT = true;
	final private static int MAX_RETRY_COUNT = 3;
	
	private WSManager wsManager;
	private static Logger logger = LogManager.getLogger(WSService.class);
	
	public WSService() {
		initConnectionListeners();
	}

	public WSManager getWSManager() {
		synchronized (WSManager.class) {
			if (wsManager == null) {
				wsManager = new WSManager(new ConnectionConfig(WS_ADDRESS, RECONNECT, MAX_RETRY_COUNT));
			}
		}
		return wsManager;
	}

	private void initConnectionListeners() {
		getWSManager().registerEventHandler(WSConnection.EventType.ON_CONNECT, () -> {
			notifyWSEventListeners(WSConnection.EventType.ON_CONNECT);
		});
		getWSManager().registerEventHandler(WSConnection.EventType.ON_CLOSE, () -> {
			notifyWSEventListeners(WSConnection.EventType.ON_CLOSE);
		});
		getWSManager().registerEventHandler(WSConnection.EventType.ON_ERROR, () -> {
			notifyWSEventListeners(WSConnection.EventType.ON_ERROR);
		});
	}

	private void notifyWSEventListeners(WSConnection.EventType wsEvent) {
		NetworkExtensionManager extMgr = NetworkExtensionManager.getInstance();
		
		Set<ICoreExtension> extensions = extMgr.getExtensions(NetworkExtensionIDs.WS_EVENT, IWSEvent.class);
		for (ICoreExtension e : extensions) {
			IWSEvent p = (IWSEvent) e;
			switch(wsEvent) {
				case ON_CLOSE:
					p.onClose();
					break;
				case ON_CONNECT:
					p.onConnect();
					break;
				case ON_ERROR:
					p.onError();
					break;
				default:
					logger.error("Notified for a WS event that is not being listened to - something is seriously wrong");
					break;
			}
		}
	}
}
