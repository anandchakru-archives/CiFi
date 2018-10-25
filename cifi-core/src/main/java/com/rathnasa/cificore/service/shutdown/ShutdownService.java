package com.rathnasa.cificore.service.shutdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ShutdownService {
	@Autowired
	private ApplicationContext context;

	@Async
	public void startShutdownSequence() throws InterruptedException {
		Thread.sleep(3000);
		((ConfigurableApplicationContext) context).close();
	}
}
