package com.rathnasa.cifimodel.prop;

public class CifiJenkins {
	/**
	 * Jenkins Url. Eg: http://192.168.1.7:8080
	 */
	private String url;
	/**
	 * Jenkins user whom the build should be triggered by
	 */
	private String user;
	/**
	 * Jenkins crumb for authentication Eg: 0fb89a7887bf0cd67b2bcf536c357396
	 * To get apiToken, go to http://192.168.1.7:8080/securityRealm/user/chakru/configure and click on Show API Token
	 */
	private String apiToken;
	/**
	 * Jenkins Build Token eg: tast6keziGINEFrecUCogEweP0sTITOjiCrLslFron4sp
	 * To Get buildToken, go to http://192.168.1.7:8080/job/<job_name>/configure enable Trigger builds remotely, then generate and enter a token in Authentication Token field.
	 */
	private String buildToken;
	/**
	 * Name of the Jenkins Job. Eg: jrvite
	 */
	private String name;

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getApiToken() {
		return apiToken;
	}
	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}
	public String getBuildToken() {
		return buildToken;
	}
	public void setBuildToken(String buildToken) {
		this.buildToken = buildToken;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
