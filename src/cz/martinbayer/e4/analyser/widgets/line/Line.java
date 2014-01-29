package cz.martinbayer.e4.analyser.widgets.line;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Composite;

import cz.martinbayer.e4.analyser.swt.utils.ColorUtils;
import cz.martinbayer.e4.analyser.widgets.SWTUtils;

public class Line extends Composite implements Serializable, ILine {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2706865167509467633L;

	/** correct */
	private Region region;

	private final int REGION_PADDING = 4;

	/**
	 * width is used to create a line of this width by gc.drawLine
	 */
	private final int DEFAULT_WIDTH = 4;

	private Color actLineColor, lineColor, lineHoverColor, actStartPointColor,
			startPointColor, startPointHoverColor, actEndPointColor,
			endPointColor, endPointHoverColor;
	/**
	 * points where the mouse is pressed - it is placed on both ends in the
	 * middle of the line. It must be recalculated to the coordinates of the
	 * Line component.
	 */
	private Point startPoint, endPoint;

	/**
	 * startPoint and endPoint values recalculated to local coordinates
	 */
	private Point localStartPoint = new Point(0, 0), localEndPoint = new Point(
			0, 0);

	/**
	 * whole component cannot be placed in (0,0) because it would cut the parts
	 * of the line. So these values are counted and used to move the component
	 * properly.
	 */
	private int offsetY = 0, offsetX = 0;

	/**
	 * Points which represent the corners if the rectangle around the line
	 */
	private Point p1, p2, p3, p4;

	/**
	 * used to hold the information about polygon created around the line. Used
	 * by mouse listener to check if the line is hovered, selected...
	 */
	private Polygon linePolygon = new Polygon();
	private Rectangle startSpotRegion, endSpotRegion;

	private Point startSpot, endSpot;

	/**
	 * used to split the functionality of the line. Listeners of the handler
	 * must be registered via Line instance by addLineEventListener (eventually
	 * removed by removeLineEventListener)
	 */
	private LineEventHandler lineHandler;

	private Rectangle startAreaRect;

	private Rectangle endAreaRect;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            - component which will contain the {@link Line}
	 * @param style
	 *            - component is always set as SWT.TRANSPARENT but additional
	 *            styles can be added by this parameter
	 */
	public Line(Composite parent, int style) {
		super(parent, style | SWT.TRANSPARENT);
		initListeners();
		setCursor(new Cursor(null, SWT.CURSOR_HAND));
		actLineColor = lineColor = ColorUtils.getColor(SWT.COLOR_BLACK);
		lineHoverColor = ColorUtils.getColor(SWT.COLOR_GRAY);
		actStartPointColor = startPointColor = ColorUtils
				.getColor(SWT.COLOR_BLACK);
		startPointHoverColor = ColorUtils.getColor(SWT.COLOR_YELLOW);
		actEndPointColor = endPointColor = ColorUtils.getColor(SWT.COLOR_BLACK);
		endPointHoverColor = ColorUtils.getColor(SWT.COLOR_YELLOW);
	}

	@Override
	public void setHighlighted(boolean highlighted, LinePart part) {
		if (part == null) {
			actLineColor = lineColor;
			actStartPointColor = startPointColor;
			actEndPointColor = endPointColor;
		} else {
			switch (part) {
			case LINE:
				if (highlighted) {
					actLineColor = lineHoverColor;
				} else {
					actLineColor = lineColor;
				}
				break;
			case START_SPOT:
				if (highlighted) {
					actStartPointColor = startPointHoverColor;
				} else {
					actStartPointColor = startPointColor;
				}
				break;
			case END_SPOT:
				if (highlighted) {
					actEndPointColor = endPointHoverColor;
				} else {
					actEndPointColor = endPointColor;
				}
				break;
			}
		}
		redraw();
	}

	private void initListeners() {
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setLineWidth(DEFAULT_WIDTH);
				e.gc.setAntialias(SWT.ON);
				e.gc.setAdvanced(true);
				e.gc.setForeground(actLineColor);
				e.gc.drawLine(localStartPoint.x + offsetX, localStartPoint.y
						+ offsetY, localEndPoint.x + offsetX, localEndPoint.y
						+ offsetY);
				e.gc.setBackground(actStartPointColor);
				e.gc.fillOval(localStartPoint.x + offsetX - DEFAULT_WIDTH,
						localStartPoint.y + offsetY - DEFAULT_WIDTH,
						2 * DEFAULT_WIDTH, 2 * DEFAULT_WIDTH);
				e.gc.setBackground(actEndPointColor);
				e.gc.fillOval(localEndPoint.x + offsetX - DEFAULT_WIDTH,
						localEndPoint.y + offsetY - DEFAULT_WIDTH,
						2 * DEFAULT_WIDTH, 2 * DEFAULT_WIDTH);
			}
		});
		lineHandler = new LineEventHandler(this);
		addMouseListener(lineHandler);
		addMouseMoveListener(lineHandler);
		addMouseTrackListener(lineHandler);
	}

	private int getMinX() {
		int minX = Math.min(
				startSpot.x,
				Math.min(endSpot.x,
						Math.min(p1.x, Math.min(p3.x, Math.min(p2.x, p4.x)))));
		return minX;
	}

	private int getMaxX() {
		int maxX = Math.max(
				startSpot.x,
				Math.max(endSpot.x,
						Math.max(p1.x, Math.max(p3.x, Math.max(p2.x, p4.x)))));
		return maxX;
	}

	private int getMinY() {
		int minY = Math.min(
				startSpot.y,
				Math.min(endSpot.y,
						Math.min(p1.y, Math.min(p3.y, Math.min(p2.y, p4.y)))));
		return minY;
	}

	private int getMaxY() {
		int maxY = Math.max(
				startSpot.y,
				Math.max(endSpot.y,
						Math.max(p1.y, Math.max(p3.y, Math.max(p2.y, p4.y)))));
		return maxY;
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		if (startSpot != null && endSpot != null && p1 != null && p3 != null
				&& p2 != null && p4 != null) {
			int minX = getMinX();
			int maxX = getMaxX();
			int minY = getMinY();
			int maxY = getMaxY();
			return new Point(Math.abs(minX - maxX) + 4,
					Math.abs(minY - maxY) + 4);
		}
		return new Point(0, 0);
	}

	private void initPolygons(Point startPoint, Point endPoint, int thickness) {
		Point tempPoint;
		Point startPointTemp = new Point(startPoint.x, startPoint.y);
		Point endPointTemp = new Point(endPoint.x, endPoint.y);
		// we need to have startPoint to be the point with lowest X
		// coordinate
		if (startPointTemp.x > endPointTemp.x) {
			tempPoint = startPointTemp;
			startPointTemp = endPointTemp;
			endPointTemp = tempPoint;
		}
		int dX = Math.abs(startPointTemp.x - endPointTemp.x);
		int dY = Math.abs(startPointTemp.y - endPointTemp.y);
		double aTan;
		aTan = Math.atan((double) dY / (double) dX);
		int offsetX = (int) Math.ceil(Math.sin(aTan) * thickness / 2);
		int offsetY = (int) Math.ceil(Math.cos(aTan) * thickness / 2);

		boolean startPointLower = startPointTemp.y > endPointTemp.y;

		// coordinates of holders on the ends of the line
		int startPointX, startPointY, endPointX, endPointY;
		if (startPointLower) {
			p1 = new Point(startPointTemp.x - offsetX, startPointTemp.y
					- offsetY);
			p2 = new Point(endPointTemp.x - offsetX, endPointTemp.y - offsetY);
			p3 = new Point(endPointTemp.x + offsetX, endPointTemp.y + offsetY);
			p4 = new Point(startPointTemp.x + offsetX, startPointTemp.y
					+ offsetY);
		} else {
			p1 = new Point(startPointTemp.x + offsetX, startPointTemp.y
					- offsetY);
			p2 = new Point(endPointTemp.x + offsetX, endPointTemp.y - offsetY);
			p3 = new Point(endPointTemp.x - offsetX, endPointTemp.y + offsetY);
			p4 = new Point(startPointTemp.x - offsetX, startPointTemp.y
					+ offsetY);
		}

		startPointX = startPointTemp.x - thickness;
		startPointY = startPointTemp.y - thickness;
		endPointX = endPointTemp.x - thickness;
		endPointY = endPointTemp.y - thickness;

		startSpot = new Point(startPointX, startPointY);
		endSpot = new Point(endPointX, endPointY);

		int minX = getMinX();
		int minY = getMinY();
		if (minX < 0) {
			this.offsetX = Math.abs(minX);
		} else {
			this.offsetY = 0;
		}
		if (minY < 0) {
			this.offsetY = Math.abs(minY);
		} else {
			this.offsetY = 0;
		}
		int[] xCoordsPolygon = new int[] { p1.x + this.offsetX,
				p2.x + this.offsetX, p3.x + this.offsetX, p4.x + this.offsetX };
		int[] yCoordPolygon = new int[] { p1.y + this.offsetY,
				p2.y + this.offsetY, p3.y + this.offsetY, p4.y + this.offsetY };

		// set actual polygon coordinates to linePolygon instance
		linePolygon.xpoints = xCoordsPolygon;
		linePolygon.ypoints = yCoordPolygon;
		linePolygon.npoints = 4;

		startSpotRegion = new Rectangle(startSpot.x + this.offsetX, startSpot.y
				+ this.offsetY, 2 * thickness, 2 * thickness);
		endSpotRegion = new Rectangle(endSpot.x + this.offsetX, endSpot.y
				+ this.offsetY, 2 * thickness, 2 * thickness);
	}

	@Override
	public void setStartPoint(int x, int y) {
		this.setStartPoint(x, y, null);
	}

	@Override
	public void setStartPoint(int x, int y, Rectangle startAreaRect) {
		this.startPoint = new Point(x, y);
		this.startAreaRect = startAreaRect;
		System.out.println("new start point:" + this.startPoint);
		fixLocalPoints();
	}

	@Override
	public void setEndPoint(int x, int y) {
		this.setEndPoint(x, y, null);
	}

	@Override
	public void setEndPoint(int x, int y, Rectangle endAreaRect) {
		this.endPoint = new Point(x, y);
		this.endAreaRect = endAreaRect;
		System.out.println("new end point:" + this.endPoint);
		fixLocalPoints();
	}

	private void fixLocalPoints() {
		if (startPoint != null && endPoint != null) {

			int minX = Math.min(startPoint.x, endPoint.x);
			int minY = Math.min(startPoint.y, endPoint.y);

			this.localStartPoint.x = startPoint.x - minX;
			this.localStartPoint.y = startPoint.y - minY;
			this.localEndPoint.x = endPoint.x - minX;
			this.localEndPoint.y = endPoint.y - minY;

			initPolygons(localStartPoint, localEndPoint, DEFAULT_WIDTH
					+ REGION_PADDING);
			region = new Region();
			int[] linePolygonSWT = SWTUtils.awtToSwtPolygon(this.linePolygon);
			region.add(linePolygonSWT);
			region.add(this.startSpotRegion);
			region.add(this.endSpotRegion);
			setRegion(region);
			region.dispose();
		} else if (startPoint != null) {
			this.localStartPoint = new Point(0, 0);
		} else {
			this.localEndPoint = new Point(0, 0);
		}
		setLocation(Line.countLocation(Line.this));
		pack();
	}

	private Point movePointsToSourceEdges(Point mainPoint, Rectangle mainRect,
			Point secondPoint) {
		// count if the intersection of the line created based on
		// mainPoint/secondPoint and rectangle is place on TOP/BOTTOM/LEFT/RIGHT
		// edges
		Point intersectionPoint = getIntersectionPoint(mainPoint, mainRect,
				secondPoint);
		return intersectionPoint;

	}

	private Point getIntersectionPoint(Point mainPoint, Rectangle mainRect,
			Point secondPoint) {

		Point p = null;
		Line2D mainLine = new Line2D.Float();
		mainLine.setLine(mainPoint.x, mainPoint.y, secondPoint.x, secondPoint.y);

		// temporary lines presenting each rectangle edge
		ArrayList<Line2D> tempLines = new ArrayList<Line2D>();
		// check top line
		tempLines.add(new Line2D.Float(mainRect.x, mainRect.y, mainRect.x
				+ mainRect.width, mainRect.y));
		// check bottom line
		tempLines.add(new Line2D.Float(mainRect.x,
				mainRect.y + mainRect.height, mainRect.x + mainRect.width,
				mainRect.y + mainRect.height));
		// check left line
		tempLines.add(new Line2D.Float(mainRect.x, mainRect.y, mainRect.x,
				mainRect.y + mainRect.height));
		// check right line
		tempLines.add(new Line2D.Float(mainRect.x + mainRect.width, mainRect.y,
				mainRect.x + mainRect.width, mainRect.y + mainRect.height));
		for (Line2D tempLine : tempLines) {
			p = getIntersectionPoint(mainLine, tempLine);
			if (p != null) {
				return p;
			}
		}
		return p;
	}

	private Point getIntersectionPoint(Line2D lineA, Line2D lineB) {

		double x1 = lineA.getX1();
		double y1 = lineA.getY1();
		double x2 = lineA.getX2();
		double y2 = lineA.getY2();

		double x3 = lineB.getX1();
		double y3 = lineB.getY1();
		double x4 = lineB.getX2();
		double y4 = lineB.getY2();

		Point p = null;

		double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		if (d != 0) {
			double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2)
					* (x3 * y4 - y3 * x4))
					/ d;
			double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2)
					* (x3 * y4 - y3 * x4))
					/ d;

			p = new Point((int) xi, (int) yi);

		}
		return p;
	}

	public static Point countLocation(Point startPoint, Point endPoint,
			int offsetX, int offsetY) {
		if (startPoint != null && endPoint != null) {
			Point leftPoint = startPoint.x <= endPoint.x ? startPoint
					: endPoint;
			Point rightPoint;

			if (leftPoint.equals(startPoint)) {
				rightPoint = endPoint;
			} else {
				rightPoint = startPoint;
			}

			Point location = new Point(leftPoint.x, -1);
			if (leftPoint.y <= rightPoint.y) {
				location.y = leftPoint.y;
			} else {
				location.y = rightPoint.y;
			}
			location.x -= offsetX;
			location.y -= offsetY;
			return location;
		}
		return new Point(-1, -1);
	}

	public static Point countLocation(Line line) {
		return countLocation(line.startPoint, line.endPoint, line.offsetX,
				line.offsetY);
	}

	@Override
	public boolean isStartSpotSelected(int x, int y) {
		return startSpotRegion.contains(x, y);
	}

	@Override
	public boolean isEndSpotSelected(int x, int y) {
		return endSpotRegion.contains(x, y);
	}

	public void moveLinePart(int moveX, int moveY, LinePart partType) {
		System.out.println("movement:" + new Point(moveX, moveY));
		switch (partType) {
		case START_SPOT:
			setStartPoint(this.startPoint.x + moveX, this.startPoint.y + moveY);
			break;
		case END_SPOT:
			setEndPoint(this.endPoint.x + moveX, this.endPoint.y + moveY);
			break;
		case LINE:
			setStartPoint(this.startPoint.x + moveX, this.startPoint.y + moveY);
			setEndPoint(this.endPoint.x + moveX, this.endPoint.y + moveY);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean isLineSelected(int x, int y) {
		return linePolygon.contains(x, y);
	}

	@Override
	public void addLineEventListener(ILineEventListener listener) {
		this.lineHandler.addListener(listener);
	}

	@Override
	public void removeLineEventListener(ILineEventListener listener) {
		this.lineHandler.removeListener(listener);
	}
}
