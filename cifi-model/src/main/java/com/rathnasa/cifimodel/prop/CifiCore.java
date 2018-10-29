package com.rathnasa.cifimodel.prop;

public class CifiCore {
	private CifiShutdown shutdown = new CifiShutdown();
	private CifiJenkins jenkins = new CifiJenkins();

	public CifiShutdown getShutdown() {
		return shutdown;
	}
	public void setShutdown(CifiShutdown shutdown) {
		this.shutdown = shutdown;
	}
	public CifiJenkins getJenkins() {
		return jenkins;
	}
	public void setJenkins(CifiJenkins jenkins) {
		this.jenkins = jenkins;
	}
}
