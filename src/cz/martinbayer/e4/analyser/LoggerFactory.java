package cz.martinbayer.e4.analyser;

import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;

public class LoggerFactory {

	private static ILoggerProvider provider;

	public static synchronized final Logger getInstance(Class<?> clazz) {
		return provider.getClassLogger(clazz);
	}

	static void initializeLoggerFactory(ILoggerProvider provider) {
		LoggerFactory.provider = provider;
	}
}
