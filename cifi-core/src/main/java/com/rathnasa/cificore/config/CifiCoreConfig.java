package com.rathnasa.cificore.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan({ "com.rathnasa.cificore" })
@EntityScan({ "com.rathnasa.cificore" })
public class CifiCoreConfig {
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}