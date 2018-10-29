package com.rathnasa.cificore.service.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import com.rathnasa.cificore.model.Constants;

@Service
@Profile(value = { "test", "local", "default" })
public class JenkinsBuildLocal implements JenkinsBuild {
	private static final Logger logger = LoggerFactory.getLogger("com.rathnasa.cificore.service.api.JenkinsBuildProd");

	@Override
	public String build(String cause) {
		logger.debug("Local Dummy Build..");
		return Constants.SUCCESS_BUILD;
	}
}
