package cz.martinbayer.e4.analyser.widgets;

import java.awt.Polygon;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

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

	public static Image getImage(String imageName) {
		Bundle bundle = FrameworkUtil.getBundle(SWTUtils.class);
		URL url = FileLocator
				.find(bundle, new Path("icons/" + imageName), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

	public static Image getImage(String imageName, Class<?> clazz) {
		Bundle bundle = FrameworkUtil.getBundle(clazz);
		URL url = FileLocator
				.find(bundle, new Path("icons/" + imageName), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

	public static Image getImage(String folderName, String imageName,
			Class<?> clazz) {
		Bundle bundle = FrameworkUtil.getBundle(clazz);
		URL url = FileLocator.find(bundle, new Path(folderName + "/"
				+ imageName), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

	public static Point getTextSize(Text t, int stringLength) {
		GC gc = new GC(t);
		FontMetrics fm = gc.getFontMetrics();
		int width = stringLength * fm.getAverageCharWidth();
		int height = fm.getHeight();
		gc.dispose();
		Point size = t.computeSize(width, height);
		return size;
	}
}
