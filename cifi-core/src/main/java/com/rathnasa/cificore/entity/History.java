package com.rathnasa.cificore.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.rathnasa.cifimodel.enums.BuildStatusType;

@Entity
@Table(name = "cifi_history")
public class History {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "history_id")
	private Long historyId;
	@JoinColumn(name = "appId", insertable = true, updatable = true)
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private App app;
	@Column(name = "version")
	private String version;
	@Column(name = "tag")
	private String tag;
	@Column(name = "commit_id")
	private String commitId;
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private BuildStatusType status;
	@Column(name = "asset_url")
	private String assetUrl;
	@Column(name = "asset_id")
	private String assetId;
	@Column(name = "latest")
	private Boolean latest;
	@Column(name = "deployed")
	private Boolean deployed;

	public Long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}
	public App getApp() {
		return app;
	}
	public void setApp(App app) {
		this.app = app;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public BuildStatusType getStatus() {
		return status;
	}
	public void setStatus(BuildStatusType status) {
		this.status = status;
	}
	public String getCommitId() {
		return commitId;
	}
	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
	public String getAssetUrl() {
		return assetUrl;
	}
	public void setAssetUrl(String assetUrl) {
		this.assetUrl = assetUrl;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public Boolean getLatest() {
		return latest;
	}
	public void setLatest(Boolean latest) {
		this.latest = latest;
	}
	public Boolean getDeployed() {
		return deployed;
	}
	public void setDeployed(Boolean deployed) {
		this.deployed = deployed;
	}
}