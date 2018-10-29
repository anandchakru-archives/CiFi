package com.rathnasa;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.rathnasa.cifisample.config.CifiSampleConfig;

@SpringBootApplication
public class CifiSample2AppInit {
	private static final Logger logger = LoggerFactory.getLogger("com.rathnasa.CifiSample2AppInit");
	private static final String SHUTDOWN_KEY = "shutdownkey.txt";
	private static final Integer MAX_RETRY = 3;
	private static final Integer PORT = 8078;
	private String SHUTDOWN_URL = "http://localhost:PORT/shutdown";
	private static final DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CifiSampleConfig.class, args);
		logger.debug("Initialized CifiSample2AppInit.");
	}
	@Bean
	CommandLineRunner runner() {
		return args -> {
			List<String> pbArgs = new ArrayList<String>();
			pbArgs.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
			pbArgs.add("-Dserver.port=" + PORT);
			pbArgs.add("-jar");
			pbArgs.add("/Users/anand/ews/CiFi/cifi-core/target/cifi-core-1.0.jar");
			if (!portAvailable(PORT)) {
				String previousKey = readKey();
				int retryCounter = 0;
				while (retryCounter++ < MAX_RETRY && !portAvailable(PORT)) {
					shutdown(previousKey, PORT);
				}
			}
			String key = UUID.randomUUID().toString().replaceAll("-", "");
			writeKey(key, null);
			pbArgs.add("--cifi.core.shutdown.key=" + key);
			pbArgs.add("--spring.profiles.active=prod");
			pbArgs.add(
					"--spring.datasource.url=jdbc:mysql://localhost:3306/cifi?useSSL=false&allowPublicKeyRetrieval=true");
			pbArgs.add("--spring.datasource.username=tester");
			pbArgs.add("--spring.datasource.password=tester");
			pbArgs.add("--spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect");
			pbArgs.add("--spring.jpa.hibernate.ddl-auto=create");
			String[] array = (String[]) pbArgs.stream().toArray(String[]::new);
			logger.debug("Command:\n" + Arrays.toString(array));
			new ProcessBuilder().inheritIO().command(array).start();
		};
	}
	private void shutdown(String key, Integer port) {
		try {
			ResponseEntity<String> response = null;
			if (key == null) {
				response = new RestTemplate().getForEntity(SHUTDOWN_URL.replace("PORT", String.valueOf(port)),
						String.class);
			} else {
				response = new RestTemplate()
						.getForEntity(SHUTDOWN_URL.replace("PORT", String.valueOf(port)) + "/" + key, String.class);
			}
			logger.debug("Shutdown HTTP:" + response.getStatusCodeValue());
			logger.debug("Shutdown RESP:" + response.getBody());
			Thread.sleep(10000);
			if (response.getBody().indexOf("Going down in 3 seconds") > 0) {
				writeKey(key, ZonedDateTime.now().format(PATTERN));
			}
		} catch (Exception e) {
			logger.debug("Guess, cifi is already shutting down.", e);
		}
	}
	public String readKey() {
		try {
			return Files.readAllLines(Paths.get(SHUTDOWN_KEY)).get(0);
		} catch (IOException e) {
			return null;
		}
	}
	public void writeKey(String key, String file) {
		try {
			Files.write(Paths.get(file == null ? SHUTDOWN_KEY : file), key.getBytes(StandardCharsets.UTF_8));
			logger.debug("Shutdown url:" + SHUTDOWN_URL.replace("PORT", String.valueOf(PORT)) + "/" + key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static boolean portAvailable(Integer port) {
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
