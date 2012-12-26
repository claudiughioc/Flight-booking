package airService.web;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Logger for AirService
 * @author Claudiu Ghioc claudiu.ghioc@gmail.com
 *
 */
public class AirServiceLogger extends Logger {
	public static final String LOG_PATH = "/var/log/tomcat/airservice.log";

	protected AirServiceLogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	}

	public static Logger getLogger(String name) {
		Logger logger;
		try {
			logger = Logger.getLogger(name);
			Handler fileHandler = new FileHandler(LOG_PATH);
			Handler consoleHandler = new ConsoleHandler();
			logger.addHandler(fileHandler);
			logger.addHandler(consoleHandler);
		} catch (Exception e) {
			return null;
		}
		return logger;
	}
}
