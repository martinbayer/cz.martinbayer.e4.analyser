package cz.martinbayer.e4.analyser.widgets.line;

public interface ILine {

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

	boolean isStartSpotSelected(int x, int y);

	boolean isEndSpotSelected(int x, int y);

	boolean isLineSelected(int x, int y);

	void addLineEventListener(ILineEventListener listener);

	void removeLineEventListener(ILineEventListener listener);

	void setStartPoint(int x, int y);

	void setEndPoint(int x, int y);

}
