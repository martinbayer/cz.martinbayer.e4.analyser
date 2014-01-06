package cz.martinbayer.e4.analyser.widgets.canvasitem;

import cz.martinbayer.e4.analyser.palette.ConnectionItem;

public interface ICanvasItem {

	void setHighlighted(boolean highlighted);

	void addCanvasItemEventListener(ICanvasItemEventListener listener);

	void removeCanvasItemEventListener(ICanvasItemEventListener listener);

	boolean addConnection(ConnectionItem connection);
}
