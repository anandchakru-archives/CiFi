package com.rathnasa.cifimodel.jenkins;

import java.io.Serializable;
import com.rathnasa.cifimodel.enums.BuildStatusType;

@SuppressWarnings("serial")
public class JenkinsJobPayload implements Serializable {
	private BuildStatusType status;
	private String commitId;
	private String assetId;
	private String version;
	private String tag;
	private String assetUrl;

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
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public String getAssetUrl() {
		return assetUrl;
	}
	public void setAssetUrl(String assetUrl) {
		this.assetUrl = assetUrl;
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
}
