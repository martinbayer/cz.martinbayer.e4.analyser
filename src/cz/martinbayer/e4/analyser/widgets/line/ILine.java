package cz.martinbayer.e4.analyser.widgets.line;

import org.eclipse.swt.graphics.Rectangle;

import cz.martinbayer.e4.analyser.widgets.ICanvasItem;

public interface ILine extends ICanvasItem {

	/**
	 * Implementation arranges each part of the line to be highlighted if
	 * needed. It can be called by listeners etc.
	 * 
	 * @param highlighted
	 *            - if TRUE, then particular part of the line is highlighted
	 * @param part
	 *            - directs which part of the line is going to be highlighted
	 */
	void setHighlighted(boolean highlighted, LinePart part);

	/**
	 * Line is selected if parameter "selected" = True, otherwise Line is
	 * unselected
	 */
	void setSelected(boolean selected);

	boolean isStartSpotSelected(int x, int y);

	boolean isEndSpotSelected(int x, int y);

	boolean isLineSelected(int x, int y);

	void setStartPoint(int x, int y);

	void setEndPoint(int x, int y);

	void setStartPoint(int x, int y, Rectangle startAreaRect);

	void setEndPoint(int x, int y, Rectangle endAreaRect);

}
