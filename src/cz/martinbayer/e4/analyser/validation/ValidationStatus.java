package cz.martinbayer.e4.analyser.validation;

public class ValidationStatus {

	private boolean isValid;
	private String message;
	private ValidationStatus nextStatus;

	public ValidationStatus(boolean isValid, String message) {
		this.isValid = isValid;
		this.message = message;
	}

	public final boolean isValid() {
		return isValid;
	}

	public final String getMessage() {
		return message;
	}

	public void append(ValidationStatus validationStatus) {
		nextStatus = validationStatus;
	}

	public final ValidationStatus getNextStatus() {
		return nextStatus;
	}

}
