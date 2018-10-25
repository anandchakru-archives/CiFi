package com.rathnasa.cificore.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "cifi_app")
public class App {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "app_id")
	private Long appId;
	@Column(name = "app_name")
	private String appName;
	@Column(name = "url")
	private String url;
	@Column(name = "api")
	private String api;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //EXCLUDE IN RESPONSE
	@Column(name = "at")
	private String at;
	@Column(name = "repo_id")
	private Long repoId;
	/**
	 * Token is the secret provided @ https://github.com/anandchakru/jrvite/settings/hooks
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "gh_token")
	private String ghToken;
	/**
	 * Token is the secret provided @ Jenkins
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "jen_token")
	private String jenToken;

	public Long getAppId() {
		return appId;
	}
	public void setAppId(Long appId) {
		this.appId = appId;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	public String getAt() {
		return at;
	}
	public void setAt(String at) {
		this.at = at;
	}
	public Long getRepoId() {
		return repoId;
	}
	public void setRepoId(Long repoId) {
		this.repoId = repoId;
	}
	public String getGhToken() {
		return ghToken;
	}
	public void setGhToken(String ghToken) {
		this.ghToken = ghToken;
	}
	public String getJenToken() {
		return jenToken;
	}
	public void setJenToken(String jenToken) {
		this.jenToken = jenToken;
	}
}