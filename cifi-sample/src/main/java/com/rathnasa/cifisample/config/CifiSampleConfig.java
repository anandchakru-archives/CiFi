package com.rathnasa.cifisample.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@SpringBootApplication
@Import({ CifiPropConfig.class })
@ComponentScan(basePackages = { "com.rathnasa.cifisample.service" })
public class CifiSampleConfig {
}