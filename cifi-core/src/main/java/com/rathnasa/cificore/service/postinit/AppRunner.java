package com.rathnasa.cificore.service.postinit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class AppRunner implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger("com.rathnasa.cificore.service.postinit.AppRunner");
	/*@Autowired
	private ApplicationContext context;
	@Autowired
	Environment env;*/

	@Override
	public void run(String... args) throws Exception {
		logger.debug(this.getClass().getCanonicalName() + " initialized.");
		//setupCifiShutdownKey(context, args);
		//banner();
	}
	/*private void banner() {
		try {
			System.out.println("\n");
			Files.readAllLines(new ClassPathResource("cifi-core.txt").getFile().toPath()).forEach(l -> {
				System.out.println(l);
			});
			System.out.println("\tProfiles\t: " + Arrays.toString(env.getActiveProfiles()) + "\n\n");
		} catch (IOException e) {
			logger.debug("FNF", e);
		}
	}*/
	/*private void setupCifiShutdownKey(ApplicationContext context, String[] args) {
		ConfigurableListableBeanFactory bf = ((ConfigurableApplicationContext) context).getBeanFactory();
		Options options = new Options();
		options.addOption("cifiShutdownKey", true, "Shutdown request should have this Key in the request.");
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			String shutdownKey = (String) cmd.getParsedOptionValue("cifiShutdownKey");
			if (shutdownKey != null) {
				bf.registerSingleton(BeanConstants.BEAN_SHUTDOWN_KEY, shutdownKey);
				logger.debug("ShutdownKey: " + shutdownKey);
			} else {
				logger.debug("shutdownKey null, Shutdown Not Registered." + Arrays.toString(args));
			}
		} catch (ParseException e) {
			logger.debug("Parsing Arguements:" + Arrays.toString(args) + "Exception:" + e);
		}
	}*/
	/*public static boolean portAvailable(String portS) {
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
					 should not be thrown 
				}
			}
		}
		return false;
	}*/
}
