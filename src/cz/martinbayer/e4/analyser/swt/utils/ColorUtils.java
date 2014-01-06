package cz.martinbayer.e4.analyser.swt.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorUtils {

	/**
	 * Get SWT color
	 * 
	 * @param colorCode
	 *            - use SWT color code like {@link SWT.COLOR_YELLOW}
	 * @return - SWT color
	 */
	public static Color getColor(int colorCode) {
		return Display.getDefault().getSystemColor(colorCode);
	}

	/**
	 * Return color based on RGB values
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @return - SWT color
	 */
	public static Color getColor(int red, int green, int blue) {
		return new Color(null, red, green, blue);
	}
}
