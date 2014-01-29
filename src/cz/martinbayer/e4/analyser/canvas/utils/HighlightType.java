package cz.martinbayer.e4.analyser.canvas.utils;

import org.eclipse.swt.SWT;

public enum HighlightType {

	ERROR(SWT.COLOR_RED), WARNING(SWT.COLOR_DARK_YELLOW), HIGHLIGHT(
			SWT.COLOR_DARK_GRAY);
	private int swtColorCode;

	private HighlightType(int swtColorCode) {
		this.swtColorCode = swtColorCode;
	}

	public int getSwtColorCode() {
		return swtColorCode;
	}

}
