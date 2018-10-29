package com.rathnasa.cifisample.service;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.rathnasa.cifimodel.prop.CifiSettings;

@Service
public class CifiStarter implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger("com.rathnasa.cifisample.service.CifiStarter");
	private static final String SDK_PATH = "shutdownkey.txt";
	private static final Integer DEFAULT_MAX_RETRY = 2;
	private String SHUTDOWN_URL = "http://localhost:PORT/shutdown";
	@Autowired
	private CifiSettings cifiSettings;

	public void run(ApplicationArguments args) throws Exception {
		logger.debug("====================\n\tCifiStarter:\n\n");
		List<String> pbArgs = new ArrayList<String>();
		if (cifiSettings != null && cifiSettings.getAgent() != null) {
			try {
				//String classpath = System.getProperty("java.class.path");
				String bootargs = cifiSettings.getBootargs() == null ? "" : cifiSettings.getBootargs();
				String port = cifiSettings.getPort() == null ? "" : String.valueOf(cifiSettings.getPort());
				String cliargs = cifiSettings.getCliargs() == null ? "" : cifiSettings.getCliargs();
				Integer maxRetryCount = cifiSettings.getMaxRetryCount() == null ? DEFAULT_MAX_RETRY
						: cifiSettings.getMaxRetryCount();
				pbArgs.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
				pbArgs.add(bootargs);
				pbArgs.add("-Dserver.port=" + port);
				pbArgs.add("-jar");
				pbArgs.add(cifiSettings.getAgent());
				pbArgs.add(cliargs);
				if (!portAvailable(port)) {
					String previousKey = readKey();
					int retryCounter = 0;
					while (retryCounter++ < maxRetryCount && !portAvailable(port)) {
						shutdown(previousKey, port);
					}
				}
				if (cifiSettings.getRandomShutdownKey()) {
					String key = UUID.randomUUID().toString().replaceAll("-", "");
					writeKey(key);
					pbArgs.add("--cifi.core.shutdown.key=" + key);
				} else {
					pbArgs.add("--cifi.core.shutdown.key=nokey");
				}
				pbArgs.add("--spring.profiles.active=" + cifiSettings.getProfiles());
				pbArgs.add(
						"--spring.datasource.url=jdbc:mysql://localhost:3306/cifi?useSSL=false&allowPublicKeyRetrieval=true");
				pbArgs.add("--spring.datasource.username=tester");
				pbArgs.add("--spring.datasource.password=tester");
				pbArgs.add("--spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect");
				pbArgs.add("--spring.jpa.hibernate.ddl-auto=create");
				//
				String[] array = (String[]) pbArgs.stream().toArray(String[]::new);
				System.out.println("Command:\n" + Arrays.toString(array));
				new ProcessBuilder().inheritIO().command(array).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void shutdown(String key, String port) {
		try {
			ResponseEntity<String> response = null;
			if (key == null) {
				response = new RestTemplate().getForEntity(SHUTDOWN_URL.replace("PORT", port), String.class);
			} else {
				response = new RestTemplate().getForEntity(SHUTDOWN_URL.replace("PORT", port) + "/" + key,
						String.class);
			}
			logger.debug("Shutdown HTTP:" + response.getStatusCodeValue());
			logger.debug("Shutdown RESP:" + response.getBody());
			Thread.sleep(10000);
		} catch (Exception e) {
			logger.debug("Guess, cifi is already shutting down.", e);
		}
	}
	public String readKey() {
		try {
			return Files.readAllLines(Paths.get(SDK_PATH)).get(0);
		} catch (IOException e) {
			return null;
		}
	}
	public void writeKey(String key) {
		try {
			Files.write(Paths.get(SDK_PATH), key.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static boolean portAvailable(String portS) {
		int port = Integer.parseInt(portS);
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}
		return false;
	}
}
