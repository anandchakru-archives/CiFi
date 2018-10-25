package com.rathnasa.cifisample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import com.rathnasa.cifisample.config.CifiSampleConfig;

public class CifiSampleInitializer {
	private static final Logger logger = LoggerFactory.getLogger("com.rathnasa.cifi.CifiSampleInitializer");

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CifiSampleConfig.class, args);
		logger.debug("Initialized CifiSample.");
	}
}