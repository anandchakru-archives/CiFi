package com.rathnasa.cificore.controller;

//import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.egit.github.core.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathnasa.cificore.config.CifiCoreConfig;
import com.rathnasa.cificore.config.CifiPropConfig;
import com.rathnasa.cificore.entity.App;
import com.rathnasa.cificore.entity.History;
import com.rathnasa.cificore.model.Constants;
import com.rathnasa.cificore.model.github.WebhookCommit;
import com.rathnasa.cificore.model.github.WebhookPushPayLoad;
import com.rathnasa.cificore.service.repo.AppRestRepo;
import com.rathnasa.cificore.service.repo.HistoryRestRepo;
import com.rathnasa.cificore.service.sign.GithubSignatureVerifier;
import com.rathnasa.cifimodel.jenkins.JenkinsJobPayload;

@WebMvcTest
@RunWith(SpringRunner.class)
@Import({ CifiCoreConfig.class, CifiPropConfig.class })
public class WebhookControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private AppRestRepo appRepo;
	@MockBean
	private HistoryRestRepo historyRepo;
	@Autowired
	private GithubSignatureVerifier signatureVerifierService;
	private final String APPNAME = "junitapp1";
	private final String GHURL = "/webhook/github/" + APPNAME;
	private final String JENKINSURL = "/webhook/jenkins/" + APPNAME;
	private final String GHTOKEN = "SomeRandomGitHubTokenGotFromhttps://github.com/<username>/<appname>/settings/hooks/<hook>";
	private final String JENKINSTOKEN = "SomeRandomJenkinsTokenShouldMatchWhatYouHardcodedInJenkinsFile";
	private final Long REPOID = 79962239l;
	private final String COMMITID = "someRndCommitIdEg0be7af80cd161a539207a64b14631ae85fec56b4";
	private final Long APPID = 1l;

	@Test
	public void testAcceptGithub() throws Exception {
		ObjectMapper json = new ObjectMapper();
		WebhookPushPayLoad request = new WebhookPushPayLoad();
		String reqStr = json.writeValueAsString(request);
		//invalid input
		this.mockMvc.perform(post(GHURL).content(reqStr + "}").contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().string(equalTo(Constants.FAIL_JSON)));
		//invalid app
		this.mockMvc.perform(post(GHURL).content(reqStr).contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().string(equalTo(Constants.FAIL_NOREF)));
		//invalid ref
		request.setRef("invalidrefs/heads/master");
		reqStr = json.writeValueAsString(request);
		this.mockMvc.perform(post(GHURL).content(reqStr).contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().string(equalTo(Constants.SUCCESS_NOBUILD)));
		//invalid app
		request.setRef("refs/heads/master");
		reqStr = json.writeValueAsString(request);
		this.mockMvc.perform(post(GHURL).content(reqStr).contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().string(equalTo(Constants.FAIL_INCORRECTREPO)));
		//
		ArrayList<App> repoData = new ArrayList<App>();
		App e1 = new App();
		e1.setAppId(APPID);
		e1.setAppName(APPNAME);
		e1.setGhToken(GHTOKEN);
		e1.setRepoId(REPOID);
		repoData.add(e1);
		when(appRepo.findAll()).thenReturn(repoData);
		Repository repository = new Repository();
		repository.setId(REPOID);
		request.setRepository(repository);
		WebhookCommit head_commit = new WebhookCommit();
		head_commit.setId("0be7af80cd161a539207a64b14631ae85fec56b4");
		request.setHead_commit(head_commit);
		reqStr = json.writeValueAsString(request);
		//Invalid Signature
		this.mockMvc
				.perform(post(GHURL).content(reqStr).header(Constants.SIGNATURE, "invalidSignature")
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(equalTo(Constants.FAIL_SIGNATURE)));
		//Valid Signature
		this.mockMvc
				.perform(post(GHURL).content(reqStr)
						.header(Constants.SIGNATURE, signatureVerifierService.sign(reqStr, GHTOKEN))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(equalTo(Constants.SUCCESS_BUILD)));
	}
	@Test
	public void testAcceptJenkins() throws Exception {
		JenkinsJobPayload request = new JenkinsJobPayload();
		ObjectMapper json = new ObjectMapper();
		String reqStr = json.writeValueAsString(request);
		//Invalid input
		this.mockMvc
				.perform(post(JENKINSURL).content(reqStr + "}")
						.header(Constants.SIGNATURE, signatureVerifierService.sign(reqStr, JENKINSTOKEN))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().string(equalTo(Constants.FAIL_JSON)));
		//invalid app
		this.mockMvc
				.perform(post(JENKINSURL).content(reqStr)
						.header(Constants.SIGNATURE, signatureVerifierService.sign(reqStr, JENKINSTOKEN))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(equalTo(Constants.FAIL_NOMATCH_APP)));
		//invalid History
		List<App> repoData = new ArrayList<>();
		App e1 = new App();
		e1.setAppId(APPID);
		e1.setAppName(APPNAME);
		e1.setJenToken(JENKINSTOKEN);
		e1.setRepoId(REPOID);
		repoData.add(e1);
		when(appRepo.findByAppName(APPNAME)).thenReturn(repoData);
		request.setCommitId(COMMITID);
		reqStr = json.writeValueAsString(request);
		this.mockMvc
				.perform(post(JENKINSURL).content(reqStr)
						.header(Constants.SIGNATURE, signatureVerifierService.sign(reqStr, JENKINSTOKEN))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(equalTo(Constants.FAIL_NOMATCH_APP_COMMIT)));
		//Valid input
		History historyData = new History();
		historyData.setApp(e1);
		when(historyRepo.findByCommitIdAndApp_AppId(COMMITID, APPID)).thenReturn(historyData);
		this.mockMvc
				.perform(post(JENKINSURL).content(reqStr)
						.header(Constants.SIGNATURE, signatureVerifierService.sign(reqStr, JENKINSTOKEN))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(equalTo(Constants.SUCCESS_JENKINS)));
	}
}
