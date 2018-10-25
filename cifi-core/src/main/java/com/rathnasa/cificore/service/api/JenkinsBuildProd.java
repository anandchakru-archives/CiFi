package com.rathnasa.cificore.service.api;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.rathnasa.cifimodel.jenkins.JenkinsCrumbResponse;
import com.rathnasa.cifimodel.prop.CifiSettings;

@Service
@Profile(value = { "prod" })
public class JenkinsBuildProd implements JenkinsBuild {
	@Autowired
	private CifiSettings cifiSettings;
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String build(String cause) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization",
				"Basic " + new String(Base64.encodeBase64("builder:ed270d7f5a7b21a370eb8326137a1e72".getBytes())));
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<JenkinsCrumbResponse> forCrumb = restTemplate.exchange(
				cifiSettings.getJenkins() + "/crumbIssuer/api/json", HttpMethod.GET, request,
				JenkinsCrumbResponse.class);
		if (forCrumb.getStatusCode().equals(HttpStatus.OK) && forCrumb.getBody() != null) {
			headers.add(forCrumb.getBody().getCrumbRequestField(), forCrumb.getBody().getCrumb());
			restTemplate.exchange(cifiSettings.getJenkins()
					+ "/job/jrvite/build?token=tast6keziGINEFrecUCogEweP0sTITOjiCrLslFron4sp" + "&cause=" + cause,
					HttpMethod.POST, request, String.class);
			return "success@building";
		} else {
			return "fail@crumb";
		}
	}
}
