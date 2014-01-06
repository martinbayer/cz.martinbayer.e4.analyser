package cz.martinbayer.e4.analyser.canvas.areas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;

import cz.martinbayer.e4.analyser.canvas.MainCanvas;

public class InputProcessorsArea implements PaintListener, ICanvasArea {

	// describes shape of the area-!!! width is counted from the canvas!!!
	private Rectangle areaRectangle = new Rectangle(0, 0, 100, -1);

	public InputProcessorsArea(MainCanvas mainCanvas) {

	}

	@Override
	public void paintControl(PaintEvent e) {
		// paint area background
		e.gc.setBackground(e.display.getSystemColor(BACKGROUND_COLOR));
		e.gc.fillRectangle(areaRectangle.x, areaRectangle.y,
				areaRectangle.width, e.height);

		// paint right border
		e.gc.setForeground(e.display.getSystemColor(BORDER_COLOR));
		e.gc.setLineWidth(BORDER_WIDTH);
		e.gc.setLineStyle(SWT.LINE_DASH);
		e.gc.drawLine(areaRectangle.x + areaRectangle.width, areaRectangle.y,
				areaRectangle.x + areaRectangle.width, areaRectangle.y
						+ e.height);
	}
}
