package com.rathnasa.cificore.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rathnasa.cificore.entity.App;
import com.rathnasa.cificore.entity.History;
import com.rathnasa.cificore.model.Constants;
import com.rathnasa.cificore.model.github.WebhookPushPayLoad;
import com.rathnasa.cificore.service.api.JenkinsBuild;
import com.rathnasa.cificore.service.repo.AppRestRepo;
import com.rathnasa.cificore.service.repo.HistoryRestRepo;
import com.rathnasa.cificore.service.sign.SignatureVerifier;
import com.rathnasa.cifimodel.enums.BuildStatusType;
import com.rathnasa.cifimodel.jenkins.JenkinsJobPayload;

@RestController
public class WebhookController {
	private static final Logger logger = LoggerFactory.getLogger("com.rathnasa.cificore.controller.WebhookController");
	@Autowired
	private SignatureVerifier signatureVerifierService;
	@Autowired
	private JenkinsBuild jenkinsBuildr;
	private Gson gson = new Gson();
	@Autowired
	private AppRestRepo appRepo;
	@Autowired
	private HistoryRestRepo historyRepo;

	@RequestMapping(value = "/webhook/github/{appId}", method = RequestMethod.POST)
	public String acceptGithub(HttpServletRequest request, @RequestBody String payload, @PathVariable String appId) {
		/*String delivery = request.getHeader(Constants.DELIVERY);
		String event = request.getHeader(Constants.EVENT);*/
		String signature = request.getHeader(Constants.SIGNATURE);
		WebhookPushPayLoad payloadObj;
		try {
			payloadObj = gson.fromJson(payload, WebhookPushPayLoad.class);
		} catch (JsonSyntaxException e) {
			return Constants.FAIL_JSON;
		}
		if (payloadObj.getRef() == null) {
			return Constants.FAIL_NOREF;
		}
		App foundApp = null;
		// When there is a push to master branch, payloadObj.getRef() = refs/heads/master
		// When a tag was created, payloadObj.getRef() = refs/tags/v1
		if (payloadObj.getRef().indexOf("refs/heads/master") == 0) { // build only for push in master branch
			Iterable<App> apps = appRepo.findAll();
			if (apps != null) {
				for (App app : apps) {
					if (app.getRepoId() == payloadObj.getRepository().getId()) {
						if (signatureVerifierService.verify(payload, signature, app.getGhToken())) {
							foundApp = app;
							break;
						} else {
							return Constants.FAIL_SIGNATURE;
						}
					}
				}
			}
			if (foundApp != null) {
				History history = new History();
				history.setApp(foundApp);
				history.setStatus(BuildStatusType.BUILDING);
				history.setCommitId(payloadObj.getHead_commit().getId());
				historyRepo.save(history);
				return jenkinsBuildr.build(payloadObj.getHead_commit().getId());
			} else {
				return Constants.FAIL_INCORRECTREPO;
			}
		} else {
			return Constants.SUCCESS_NOBUILD;
		}
	}
	@RequestMapping(value = "/webhook/jenkins/{appId}", method = RequestMethod.POST)
	public String acceptJenkins(HttpServletRequest request, @RequestBody String payload, @PathVariable String appId) {
		/*String delivery = request.getHeader(Constants.DELIVERY);
		String event = request.getHeader(Constants.EVENT);*/
		String signature = request.getHeader(Constants.SIGNATURE);
		JenkinsJobPayload payloadObj;
		try {
			logger.debug("appid:" + appId + ", wh: jenkins," + "payload:" + payload + ",signature:" + signature);
			payloadObj = gson.fromJson(payload, JenkinsJobPayload.class);
		} catch (JsonSyntaxException e) {
			return Constants.FAIL_JSON;
		}
		List<App> findByAppName = appRepo.findByAppName(appId);
		if (findByAppName != null && !findByAppName.isEmpty()) {
			App app = findByAppName.get(0);
			boolean verify = signatureVerifierService.verify(payload, signature, app.getJenToken());
			if (verify) {
				History history = historyRepo.findByCommitIdAndApp_AppId(payloadObj.getCommitId(), app.getAppId());
				if (history != null) {
					historyRepo.setFixedLatest(false);
					historyRepo.setFixedAssetStatusTagVersionLatestFor(history.getHistoryId(), payloadObj.getAssetId(),
							payloadObj.getAssetUrl(), payloadObj.getStatus(), payloadObj.getTag(),
							payloadObj.getVersion(), true);
					return Constants.SUCCESS_JENKINS;
				} else {
					logger.debug("Couldn't find:" + app.getAppId() + ",and commit:" + payloadObj.getCommitId());
					return Constants.FAIL_NOMATCH_APP_COMMIT;
				}
			} else {
				logger.debug("Signature Verify failed");
				return Constants.FAIL_SIGNATURE;
			}
		} else {
			logger.debug("Couldn't find app:" + appId);
			return Constants.FAIL_NOMATCH_APP;
		}
	}
}