package com.rathnasa.cificore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import com.rathnasa.cifimodel.prop.CifiSettings;

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "classpath:somerandom.properties")
public class CifiPropConfig {
	@Bean
	@ConfigurationProperties(prefix = "cifi", ignoreUnknownFields = true)
	public CifiSettings cifiSettings() {
		return new CifiSettings();
	}
}