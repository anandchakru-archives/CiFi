package com.rathnasa.cificore.service.api;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(value = { "test", "local", "default" })
public class JenkinsBuildLocal implements JenkinsBuild {
	@Override
	public String build(String cause) {
		System.out.println("trigger dummy build");
		return "success@building";
	}
}
