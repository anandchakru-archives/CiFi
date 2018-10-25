package com.rathnasa.cificore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import com.rathnasa.cificore.config.CifiCoreConfig;
import com.rathnasa.cificore.config.CifiPropConfig;

@EnableAsync
@SpringBootApplication
@Import({ CifiCoreConfig.class, CifiPropConfig.class })
public class CifiCoreInitializer {
	private static final Logger logger = LoggerFactory.getLogger("com.rathnasa.cificore.CifiCoreInitializer");

	public static void main(String[] args) throws Exception {
		for (String arg : args) {
			System.out.println("cifi-core arg:" + arg);
		}
		SpringApplication.run(CifiCoreInitializer.class, args);
		logger.debug("Initialized CifiCore.");
	}
}