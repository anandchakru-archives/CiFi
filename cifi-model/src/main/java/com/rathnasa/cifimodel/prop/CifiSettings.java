package com.rathnasa.cifimodel.prop;

public class CifiSettings {
	private String agent;
	private Integer port;
	private String cliargs;
	private String bootargs;
	private Boolean randomShutdownKey;
	private Integer maxRetryCount;
	private String jenkins;
	private String profiles;
	private CifiCore core;

	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getCliargs() {
		return cliargs;
	}
	public void setCliargs(String cliargs) {
		this.cliargs = cliargs;
	}
	public String getBootargs() {
		return bootargs;
	}
	public void setBootargs(String bootargs) {
		this.bootargs = bootargs;
	}
	public String getJenkins() {
		return jenkins;
	}
	public void setJenkins(String jenkins) {
		this.jenkins = jenkins;
	}
	public Boolean getRandomShutdownKey() {
		return randomShutdownKey;
	}
	public void setRandomShutdownKey(Boolean randomShutdownKey) {
		this.randomShutdownKey = randomShutdownKey;
	}
	public Integer getMaxRetryCount() {
		return maxRetryCount;
	}
	public void setMaxRetryCount(Integer maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}
	public String getProfiles() {
		return profiles;
	}
	public void setProfiles(String profiles) {
		this.profiles = profiles;
	}
	public CifiCore getCore() {
		return core;
	}
	public void setCore(CifiCore core) {
		this.core = core;
	}
}
