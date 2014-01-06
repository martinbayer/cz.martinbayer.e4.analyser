package cz.martinbayer.e4.analyser.widgets.canvasitem;

import java.util.ArrayList;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;

public class CanvasItemEventHandler implements MouseMoveListener,
		MouseListener, MouseTrackListener {

	public static final String SELECTED_CANVAS_ITEM = "selected_canvas_item";

	private ICanvasItem item;

	private ArrayList<ICanvasItemEventListener> listeners = new ArrayList<>();

	public CanvasItemEventHandler(ICanvasItem item) {
		this.item = item;
	}

	@Override
	public void mouseMove(MouseEvent e) {
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDown(MouseEvent e) {
		fireItemSelected(e, true);
	}

	@Override
	public void mouseUp(MouseEvent e) {
		fireItemSelected(e, false);
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		item.setHighlighted(true);
		fireItemVisited(e);
	}

	@Override
	public void mouseExit(MouseEvent e) {
		item.setHighlighted(false);
	}

	@Override
	public void mouseHover(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void addListener(ICanvasItemEventListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(ICanvasItemEventListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	private void fireItemVisited(MouseEvent e) {
		CanvasItemActionEvent event = new CanvasItemActionEvent(this.item,
				null, e);
		for (ICanvasItemEventListener l : this.listeners) {
			l.itemVisited(event);
		}
	}

	private void fireItemSelected(MouseEvent e, boolean selected) {
		CanvasItemActionEvent event = new CanvasItemActionEvent(this.item,
				null, e);
		for (ICanvasItemEventListener l : this.listeners) {
			l.itemSelected(event, selected);
		}
	}
}
