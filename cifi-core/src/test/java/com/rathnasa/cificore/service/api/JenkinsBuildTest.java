package com.rathnasa.cificore.service.api;

import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import com.rathnasa.cificore.config.CifiCoreConfig;
import com.rathnasa.cificore.config.CifiPropConfig;
import com.rathnasa.cificore.model.Constants;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({ CifiCoreConfig.class, CifiPropConfig.class })
public class JenkinsBuildTest {
	@Autowired
	private JenkinsBuild jenkins;

	@BeforeClass
	public static void setupTest() {
		System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, "prod");
		System.setProperty("cifi.core.jenkins.url", "http://192.168.1.7:8080");
		System.setProperty("cifi.core.jenkins.name", "jrvite");
		System.setProperty("cifi.core.jenkins.user", "builder");
		System.setProperty("cifi.core.jenkins.apiToken", "0fb89a7887bf0cd67b2bcf536c357396");
		System.setProperty("cifi.core.jenkins.buildToken", "tast6keziGINEFrecUCogEweP0sTITOjiCrLslFron4sp");
		System.setProperty("cifi.core.shutdown.key", "nokey");
	}
	@Test
	public void testBuild() {
		assertTrue("jenkins Build failed", Constants.SUCCESS_BUILD.equals(jenkins.build("JUNIT")));
	}
}
