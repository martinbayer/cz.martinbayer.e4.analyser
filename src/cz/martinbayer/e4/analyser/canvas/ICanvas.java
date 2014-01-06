package cz.martinbayer.e4.analyser.canvas;

import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItem;
import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItemActionEvent;
import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.line.connection.ConnectionItem;

public interface ICanvas {
	void setSelectedItem(CanvasItemActionEvent event, boolean selected);

	CanvasItem getSelectedItem();

	void setSelectedConnection(ILine iLine);

	ConnectionItem getSelectedConnection();

	void setVisitedItem(CanvasItem source);
}
