package com.rathnasa.cificore.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.rathnasa.cificore.service.shutdown.ShutdownService;
import com.rathnasa.cifimodel.prop.CifiSettings;
import com.rathnasa.cifimodel.req.LiveUpdateShutdownKeyReq;

@RestController
public class ShutdownController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ShutdownService shutdownService;
	@Autowired
	private CifiSettings cifiSettings;

	@RequestMapping(value = "/shutdown/{currentKey}", method = RequestMethod.GET)
	public String shutdownContext(@PathVariable(required = false) String currentKey) throws InterruptedException {
		String currentKeyInFile = cifiSettings.getCore().getShutdown().getKey();
		if (StringUtils.isEmpty(currentKey)) {
			currentKey = "nokey";
		}
		logger.debug("Shutdown Requested with: " + currentKeyInFile + ":" + currentKey);
		if (currentKeyInFile == null || !currentKeyInFile.equals(currentKey)) {
			return "FAIL";
		} else {
			shutdownService.startShutdownSequence();
			return System.currentTimeMillis() + ": Going down in 3 seconds.";
		}
	}
	@RequestMapping(value = "/shutdown/{currentKey}", method = RequestMethod.POST)
	public String shutdownKeyLiveUpdate(@PathVariable String currentKey,
			@RequestBody LiveUpdateShutdownKeyReq liveUpdateShutdownKeyReq) throws InterruptedException {
		String currentKeyInFile = cifiSettings.getCore().getShutdown().getKey();
		logger.debug("Update Shutdown Key Requested with: " + currentKeyInFile + ":" + currentKey);
		if (currentKeyInFile == null || currentKey == null || liveUpdateShutdownKeyReq == null
				|| !currentKeyInFile.equals(currentKey)) {
			return "FAIL";
		} else {
			cifiSettings.getCore().getShutdown().setKey(liveUpdateShutdownKeyReq.getNewKey());
			return System.currentTimeMillis() + ": Updated.";
		}
	}
}
