package com.rathnasa.cifimodel.req;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LiveUpdateShutdownKeyReq implements Serializable {
	private String newKey;

	public LiveUpdateShutdownKeyReq() {
		super();
	}
	public LiveUpdateShutdownKeyReq(String newKey) {
		super();
		this.newKey = newKey;
	}
	public String getNewKey() {
		return newKey == null ? "nokey" : newKey;
	}
	public void setNewKey(String newKey) {
		this.newKey = newKey;
	}
}