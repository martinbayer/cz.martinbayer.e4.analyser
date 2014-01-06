package cz.martinbayer.e4.analyser.canvas;

import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItem;
import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItemActionEvent;
import cz.martinbayer.e4.analyser.widgets.canvasitem.ICanvasItemEventListener;

public class CanvasItemEventListener implements ICanvasItemEventListener {

	private ICanvas canvas;

	public CanvasItemEventListener(ICanvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void itemVisited(CanvasItemActionEvent e) {
		this.canvas.setVisitedItem((CanvasItem) e.getSource());
	}

	@Override
	public void itemSelected(CanvasItemActionEvent event, boolean selected) {
		this.canvas.setSelectedItem(event, selected);
	}
}
