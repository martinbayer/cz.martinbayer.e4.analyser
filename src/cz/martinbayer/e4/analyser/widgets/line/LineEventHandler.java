package cz.martinbayer.e4.analyser.widgets.line;

import java.util.ArrayList;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.TypedEvent;

public class LineEventHandler implements MouseMoveListener, MouseListener,
		MouseTrackListener {

	private ILine line;
	private ArrayList<ILineEventListener> listeners = new ArrayList<>();

	public LineEventHandler(ILine line) {
		this.line = line;
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		if (line.isStartSpotSelected(e.x, e.y)) {
			line.setHighlighted(true, LinePart.START_SPOT);
		} else if (line.isEndSpotSelected(e.x, e.y)) {
			line.setHighlighted(true, LinePart.END_SPOT);
		} else if (line.isLineSelected(e.x, e.y)) {
			line.setHighlighted(true, LinePart.LINE);
		}
	}

	@Override
	public void mouseExit(MouseEvent e) {
		line.setHighlighted(false, null);
	}

	@Override
	public void mouseHover(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (line.isStartSpotSelected(e.x, e.y)) {
			fireMouseDown(LineEventType.START_POINT_CLICKED, e);
		} else if (line.isEndSpotSelected(e.x, e.y)) {
			fireMouseDown(LineEventType.END_POINT_CLICKED, e);
		} else if (line.isLineSelected(e.x, e.y)) {
			fireMouseDown(LineEventType.LINE_CLICKED, e);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (line.isStartSpotSelected(e.x, e.y)) {
			fireMouseUp(LineEventType.START_POINT_RELEASED, e);
		} else if (line.isEndSpotSelected(e.x, e.y)) {
			fireMouseUp(LineEventType.END_POINT_RELEASED, e);
		} else if (line.isLineSelected(e.x, e.y)) {
			fireMouseUp(LineEventType.LINE_RELEASED, e);
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private void fireMouseDown(LineEventType eventType, TypedEvent originalEvent) {
		LineActionEvent event = new LineActionEvent(this.line, eventType,
				originalEvent, null);
		for (ILineEventListener l : listeners) {
			l.handleMouseDown(event);
		}
	}

	private void fireMouseUp(LineEventType eventType, TypedEvent originalEvent) {
		LineActionEvent event = new LineActionEvent(this.line, eventType,
				originalEvent, null);
		for (ILineEventListener l : listeners) {
			l.handleMouseUp(event);
		}
	}

	public void addListener(ILineEventListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(ILineEventListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
}
