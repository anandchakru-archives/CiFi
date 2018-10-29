package com.rathnasa.cificore.service.postinit;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class AppRunner implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger("com.rathnasa.cificore.service.postinit.AppRunner");

	@Override
	public void run(String... args) throws Exception {
		logger.debug(this.getClass().getCanonicalName() + " initialized with:" + Arrays.toString(args));
	}
}