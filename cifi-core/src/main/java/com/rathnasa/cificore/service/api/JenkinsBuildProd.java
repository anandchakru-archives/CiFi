package com.rathnasa.cificore.service.api;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.rathnasa.cificore.model.Constants;
import com.rathnasa.cifimodel.jenkins.JenkinsCrumbResponse;
import com.rathnasa.cifimodel.prop.CifiSettings;

/**
 * Gets the crumb from Jenkins first and then triggers the build api to start a build
 * Ref: https://support.cloudbees.com/hc/en-us/articles/219257077-CSRF-Protection-Explained
 * Ref: https://stackoverflow.com/a/21920398/234110
 * 
 * @author chakru
 *
 */
@Service
@Profile(value = { "prod" })
public class JenkinsBuildProd implements JenkinsBuild {
	private static final Logger logger = LoggerFactory.getLogger("com.rathnasa.cificore.service.api.JenkinsBuildProd");
	@Autowired
	private CifiSettings cifiSettings;
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String build(String cause) {
		HttpHeaders headers = new HttpHeaders();
		String userAndCrumb = cifiSettings.getCore().getJenkins().getUser() + ":"
				+ cifiSettings.getCore().getJenkins().getApiToken();
		headers.add("Authorization", "Basic " + new String(Base64.encodeBase64(userAndCrumb.getBytes())));
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<JenkinsCrumbResponse> forCrumb = restTemplate.exchange(
				cifiSettings.getCore().getJenkins().getUrl() + "/crumbIssuer/api/json", HttpMethod.GET, request,
				JenkinsCrumbResponse.class);
		if (forCrumb.getStatusCode().equals(HttpStatus.OK) && forCrumb.getBody() != null) {
			headers.add(forCrumb.getBody().getCrumbRequestField(), forCrumb.getBody().getCrumb());
			ResponseEntity<String> exchange = restTemplate.exchange(
					cifiSettings.getCore().getJenkins().getUrl() + "/job/"
							+ cifiSettings.getCore().getJenkins().getName() + "/build?token="
							+ cifiSettings.getCore().getJenkins().getBuildToken() + "&cause=" + cause,
					HttpMethod.POST, request, String.class);
			logger.debug(exchange.getBody());
			return Constants.SUCCESS_BUILD;
		} else {
			return Constants.FAIL_BUILD_CRUMB;
		}
	}
}
