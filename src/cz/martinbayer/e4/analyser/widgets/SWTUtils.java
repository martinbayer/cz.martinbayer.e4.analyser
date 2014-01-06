package cz.martinbayer.e4.analyser.widgets;

import java.awt.Polygon;

import org.eclipse.swt.graphics.Rectangle;

public class SWTUtils {

	public static int[] awtToSwtPolygon(Polygon polygon) {
		int[] resultPoly = new int[polygon.npoints * 2];
		for (int i = 0; i < polygon.xpoints.length; i++) {
			resultPoly[i * 2] = polygon.xpoints[i];
			resultPoly[i * 2 + 1] = polygon.ypoints[i];
		}
		return resultPoly;
	}

	public static boolean isRectangleSelected(Rectangle rectangle, int xCoord,
			int yCoord) {
		return rectangle.contains(xCoord, yCoord);
	}

	public static boolean isPolygonSelected(Polygon polygon, int xCoord,
			int yCoord) {
		return polygon.contains(xCoord, yCoord);
	}

}
