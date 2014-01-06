package cz.martinbayer.e4.analyser.widgets.canvasitem;

import cz.martinbayer.e4.analyser.widgets.line.connection.ItemConnectionConnector;

public interface ICanvasItem {

	void setHighlighted(boolean highlighted);

	void addCanvasItemEventListener(ICanvasItemEventListener listener);

	void removeCanvasItemEventListener(ICanvasItemEventListener listener);

	boolean addConnection(ItemConnectionConnector connector);
}
