package cz.martinbayer.e4.analyser.widgets.canvasitem;

public interface ICanvasItemEventListener {

	void itemVisited(CanvasItemActionEvent e);

	void itemSelected(CanvasItemActionEvent event, boolean selected);
}
