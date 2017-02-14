package org.code.toboggan.core.extension;

public class APIFailureEvent {
	private Object eventData;
	private String extensionPointId;
	
	public APIFailureEvent(String extensionPointId) {
		this.extensionPointId = extensionPointId;
	}
	
	public String getExtensionPointId() {
		return this.extensionPointId;
	}
	
	public void setData(Object o) {
		this.eventData = o;
	}
	
	public Object getData() {
		return this.eventData;
	}
}
