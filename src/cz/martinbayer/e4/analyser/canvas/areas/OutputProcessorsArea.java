package cz.martinbayer.e4.analyser.canvas.areas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;

import cz.martinbayer.e4.analyser.canvas.MainCanvas;

public class OutputProcessorsArea implements PaintListener, ICanvasArea {

	// x position and height need to be counted from event
	private Rectangle areaRectangle = new Rectangle(-1, 0, 100, -1);

	public OutputProcessorsArea(MainCanvas mainCanvas) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void paintControl(PaintEvent e) {
		// paint area background
		e.gc.setBackground(e.display.getSystemColor(BACKGROUND_COLOR));
		e.gc.fillRectangle(e.x + e.width - areaRectangle.width,
				areaRectangle.y, areaRectangle.width, e.height);

		// paint right border
		e.gc.setForeground(e.display.getSystemColor(BORDER_COLOR));
		e.gc.setLineWidth(BORDER_WIDTH);
		e.gc.setLineStyle(SWT.LINE_DASH);
		e.gc.drawLine(e.width - areaRectangle.width, areaRectangle.y, e.width
				- areaRectangle.width, areaRectangle.y + e.height);
	}

}
