package com.rathnasa.cificore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import com.rathnasa.cificore.config.CifiCoreConfig;
import com.rathnasa.cificore.config.CifiPropConfig;
import com.rathnasa.cifimodel.req.LiveUpdateShutdownKeyReq;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Import({ CifiCoreConfig.class, CifiPropConfig.class })
public class ShutdownControllerTest {
	@LocalServerPort
	private int port;
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private ShutdownController controller;

	@BeforeClass
	public static void setupArgs() {
		System.setProperty("cifi.core.shutdown.key", "correctKey");
		System.setProperty("profiles.active", "prod");
		System.setProperty("datasource.url",
				"jdbc:mysql://localhost:3306/cifi?useSSL=false&allowPublicKeyRetrieval=true");
		System.setProperty("datasource.username", "tester");
		System.setProperty("datasource.password", "tester");
		System.setProperty("jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect");
		System.setProperty("jpa.hibernate.ddl-auto", "create");
	}
	@Test
	public void contexLoads() throws Exception {
		assertThat(controller).isNotNull();
	}
	@Test
	public void testInOrder() {
		testShutdownContext();
		testShutdownKeyLiveUpdate();
	}
	private void testShutdownContext() {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/shutdown/wrongKey", String.class))
				.contains("FAIL");
	}
	private void testShutdownKeyLiveUpdate() {
		LiveUpdateShutdownKeyReq request = new LiveUpdateShutdownKeyReq("newCorrectKey");
		assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/shutdown/wrongKey", request,
				String.class)).contains("FAIL");
		assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/shutdown/correctKey", request,
				String.class)).contains("Updated");
		assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/shutdown/correctKey", request,
				String.class)).contains("FAIL");
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/shutdown/correctKey", String.class))
				.contains("FAIL");
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/shutdown/newCorrectKey", String.class))
				.contains("Going down in 3 seconds");
	}
}
