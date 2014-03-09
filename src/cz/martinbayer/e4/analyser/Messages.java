package cz.martinbayer.e4.analyser;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "cz.martinbayer.e4.analyser.messages"; //$NON-NLS-1$
	public static String connectioncannotbeadded;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
