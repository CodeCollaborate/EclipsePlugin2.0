package org.code.toboggan.network;

import clientcore.websocket.WSManager;
import clientcore.websocket.models.ConnectionConfig;

public class WSService {
	final private static String WS_ADDRESS = "wss://codecollaborate.obsessiveorange.com:8000/ws/";
	final private static boolean RECONNECT = true;
	final private static int MAX_RETRY_COUNT = 3;
	
	private static WSManager wsManager;

	public static WSManager getWSManager() {
		synchronized(WSManager.class) {
			if (wsManager == null) {
				wsManager = new WSManager(new ConnectionConfig(WS_ADDRESS, RECONNECT, MAX_RETRY_COUNT));
			}
		}
		return wsManager;
	}

	private WSService() {
		
	}
}
