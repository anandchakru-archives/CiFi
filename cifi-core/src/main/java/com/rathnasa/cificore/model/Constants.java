package com.rathnasa.cificore.model;

public class Constants {
	public static final String DELIVERY = "X-GitHub-Delivery";
	public static final String EVENT = "X-GitHub-Event";
	public static final String SIGNATURE = "X-Hub-Signature";
	public static final String E_GENERIC_ERROR = "error";
	public static final String SUCCESS_BUILD = "success@building";
	public static final String SUCCESS_NOBUILD = "success@nobuild";
	public static final String SUCCESS_JENKINS = "success@jenkins";
	public static final String FAIL_JSON = "fail@JsonSyntaxException";
	public static final String FAIL_INCORRECTREPO = "fail@incorrectRepo";
	public static final String FAIL_NOREF = "fail@noRef";
	public static final String FAIL_NOMATCH_APP_COMMIT = "fail@noAppIdCommitId";
	public static final String FAIL_NOMATCH_APP = "fail@noAppId";
	public static final String FAIL_SIGNATURE = "fail@signature";
	public static final String FAIL_BUILD_CRUMB = "fail@crumb";
	public static final String FAIL_UNKNOWN = "fail@unknown";

	private Constants() {
	}
}
