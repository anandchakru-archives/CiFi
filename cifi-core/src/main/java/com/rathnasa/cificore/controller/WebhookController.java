package com.rathnasa.cificore.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
import com.rathnasa.cificore.service.sign.GithubSignatureVerifier;
import com.rathnasa.cifimodel.enums.BuildStatusType;
import com.rathnasa.cifimodel.jenkins.JenkinsJobPayload;

@RestController
public class WebhookController {
	@Autowired
	private GithubSignatureVerifier signatureVerifierService;
	@Autowired
	private JenkinsBuild triggerService;
	private Gson gson = new Gson();
	@Autowired
	private AppRestRepo appRepo;
	@Autowired
	private HistoryRestRepo historyRepo;

	@RequestMapping(value = "/webhook/github/jrvite", method = RequestMethod.POST)
	public String acceptGithub(HttpServletRequest request, @RequestBody String payload) {
		/*String delivery = request.getHeader(Constants.DELIVERY);
		String event = request.getHeader(Constants.EVENT);*/
		String signature = request.getHeader(Constants.SIGNATURE);
		WebhookPushPayLoad payloadObj;
		try {
			payloadObj = gson.fromJson(payload, WebhookPushPayLoad.class);
		} catch (JsonSyntaxException e) {
			return "fail@JsonSyntaxException";
		}
		if (payloadObj != null) {
			if (payloadObj.getRef() == null) {
				return "fail@noRef";
			}
			App foundApp = null;
			if (payloadObj.getRef().indexOf("refs/heads/master") == 0) { // refs/heads/master | refs/tags/v1
				Iterable<App> apps = appRepo.findAll();
				for (App app : apps) {
					if (app.getRepoId() == payloadObj.getRepository().getId()
							&& signatureVerifierService.verify(payload, signature, app.getGhToken())) {
						foundApp = app;
						break;
					}
				}
				if (foundApp != null) {
					History history = new History();
					history.setApp(foundApp);
					history.setStatus(BuildStatusType.BUILDING);
					history.setCommitId(payloadObj.getHead_commit().getId());
					historyRepo.save(history);
					return triggerService.build(payloadObj.getHead_commit().getId());
				} else {
					return "fail@incorrectRepo";
				}
			} else {
				return "success@nobuild";
			}
		} else {
			return "fail@signature";
		}
	}
	@RequestMapping(value = "/webhook/jenkins/{appId}", method = RequestMethod.POST)
	public String acceptJenkins(HttpServletRequest request, @RequestBody String payload, @PathVariable String appId) {
		/*String delivery = request.getHeader(Constants.DELIVERY);
		String event = request.getHeader(Constants.EVENT);*/
		String signature = request.getHeader(Constants.SIGNATURE);
		JenkinsJobPayload payloadObj;
		try {
			System.out.println("appid:" + appId + ", wh: jenkins," + "payload:" + payload + ",signature:" + signature);
			payloadObj = gson.fromJson(payload, JenkinsJobPayload.class);
		} catch (JsonSyntaxException e) {
			return "fail@JsonSyntaxException";
		}
		if (payloadObj != null) {
			List<App> findByAppName = appRepo.findByAppName(appId);
			if (findByAppName != null && !findByAppName.isEmpty()) {
				App app = findByAppName.get(0);
				boolean verify = signatureVerifierService.verify(payload, signature, app.getJenToken());
				if (verify) {
					History history = historyRepo.findByCommitIdAndApp_AppId(payloadObj.getCommitId(), app.getAppId());
					if (history != null) {
						historyRepo.setFixedLatest(false);
						historyRepo.setFixedAssetStatusTagVersionLatestFor(history.getHistoryId(),
								payloadObj.getAssetId(), payloadObj.getAssetUrl(), payloadObj.getStatus(),
								payloadObj.getTag(), payloadObj.getVersion(), true);
					} else {
						System.out
								.println("Couldn't find:" + app.getAppId() + ",and commit:" + payloadObj.getCommitId());
					}
				} else {
					System.out.println("Couldnt find app:" + appId);
				}
			} else {
				System.out.println("Signature Verify failed");
			}
		}
		return "success@jenkins";
	}
}