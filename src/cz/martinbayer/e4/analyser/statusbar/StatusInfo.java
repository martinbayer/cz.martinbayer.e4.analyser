package cz.martinbayer.e4.analyser.statusbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusInfo {

	private String statusMessage;

	/* Status is not cleaned automatically if this property is set to True */
	private boolean doNotClean = false;

	public final String getStatusMessage() {
		return statusMessage;
	}

	public final StatusInfo setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
		return this;
	}

	public final StatusInfo setStatusMessage(String statusMessage,
			boolean doNotClean) {
		this.statusMessage = statusMessage;
		this.doNotClean = doNotClean;
		return this;
	}

	public final StatusInfo setStatusMessage(String format, Object... args) {
		this.statusMessage = String.format(parseFormat(format, args), args);
		return this;
	}

	public final boolean isDoNotClean() {
		return doNotClean;
	}

	/**
	 * If there are any groups like {0}, {1}, {2} etc contained in format, it is
	 * replaced by '%s' to be easily used by String.format function
	 */
	private String parseFormat(String format, Object... args) {
		String result = format;
		Pattern pattern = Pattern.compile("\\{\\d\\}");
		Matcher matcher = pattern.matcher(format);
		result = matcher.replaceAll("%s");
		return result;
	}
}
