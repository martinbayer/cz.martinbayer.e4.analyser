package cz.martinbayer.e4.analyser.widgets.line;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.canvas.event.LineEvent;
import cz.martinbayer.e4.analyser.widgets.ICanvasItemEventListener;

public class LineEventHandler implements MouseMoveListener, MouseListener,
		MouseTrackListener, ICanvasItemEventListener {

	private ILine line;
	private IEclipseContext ctx;
	private IEventBroker broker;

	public LineEventHandler(ILine line, IEclipseContext ctx) {
		this.line = line;
		this.ctx = ctx;
		this.broker = ctx.get(IEventBroker.class);
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		if (line.isStartSpotSelected(e.x, e.y)) {
			broker.send(ContextVariables.CANVAS_ITEM_HOVERED,
					new LineEvent<ILine>(line, e, LinePart.START_SPOT));
		} else if (line.isEndSpotSelected(e.x, e.y)) {
			broker.send(ContextVariables.CANVAS_ITEM_HOVERED,
					new LineEvent<ILine>(line, e, LinePart.END_SPOT));
		} else if (line.isLineSelected(e.x, e.y)) {
			broker.send(ContextVariables.CANVAS_ITEM_HOVERED,
					new LineEvent<ILine>(line, e, LinePart.LINE));
		}
	}

	@Override
	public void mouseExit(MouseEvent e) {
		broker.send(ContextVariables.CANVAS_ITEM_HOVERED, null);
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (line.isStartSpotSelected(e.x, e.y)) {
			this.broker.send(ContextVariables.CANVAS_CONNECTION_TAKEN,
					new LineEvent<ILine>(line, e, LinePart.START_SPOT));
			this.broker.send(ContextVariables.CANVAS_CONNECTION_SELECTED,
					new LineEvent<ILine>(line, e, LinePart.START_SPOT));
		} else if (line.isEndSpotSelected(e.x, e.y)) {
			this.broker.send(ContextVariables.CANVAS_CONNECTION_TAKEN,
					new LineEvent<ILine>(line, e, LinePart.END_SPOT));
			this.broker.send(ContextVariables.CANVAS_CONNECTION_SELECTED,
					new LineEvent<ILine>(line, e, LinePart.END_SPOT));
		} else if (line.isLineSelected(e.x, e.y)) {
			this.broker.send(ContextVariables.CANVAS_CONNECTION_TAKEN,
					new LineEvent<ILine>(line, e, LinePart.LINE));
			this.broker.send(ContextVariables.CANVAS_CONNECTION_SELECTED,
					new LineEvent<ILine>(line, e, LinePart.LINE));
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		this.broker.send(ContextVariables.CANVAS_CONNECTION_TAKEN, null);
	}

	@Override
	public void mouseMove(MouseEvent e) {
	}

	@Override
	public void itemDisposed() {
		this.broker.send(ContextVariables.CANVAS_DISPOSED_ITEM,
				new CanvasEvent<ILine>(line, null));
	}
}
