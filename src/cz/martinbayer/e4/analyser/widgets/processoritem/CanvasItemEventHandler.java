package cz.martinbayer.e4.analyser.widgets.processoritem;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.widgets.ICanvasItemEventListener;

public class CanvasItemEventHandler implements MouseMoveListener,
		MouseListener, MouseTrackListener, ICanvasItemEventListener {

	protected ICanvasManager canvasManager;
	private IProcessorItem item;
	private IEclipseContext context;
	private IEventBroker broker;

	public CanvasItemEventHandler(IProcessorItem item, IEclipseContext context) {
		this.item = item;
		this.context = context;
		broker = context.get(IEventBroker.class);
	}

	@Override
	public void mouseMove(MouseEvent e) {
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		this.item.getItem().mouseDoubleClicked(e);
	}

	@Override
	public void mouseDown(MouseEvent e) {
		CanvasEvent<IProcessorItem> event = new CanvasEvent<>(item, e);
		broker.send(ContextVariables.CANVAS_PROCESSOR_SELECTED, event);
		broker.send(ContextVariables.CANVAS_PROCESSOR_TAKEN, event);
	}

	@Override
	public void mouseUp(MouseEvent e) {
		broker.send(ContextVariables.CANVAS_PROCESSOR_TAKEN, null);
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		CanvasEvent<IProcessorItem> event = new CanvasEvent<>(item, e);
		broker.send(ContextVariables.CANVAS_ITEM_HOVERED, event);
	}

	@Override
	public void mouseExit(MouseEvent e) {
		broker.send(ContextVariables.CANVAS_ITEM_HOVERED, null);
		broker.send(ContextVariables.CANVAS_PROCESSOR_TAKEN, null);
	}

	@Override
	public void mouseHover(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemDisposed() {
		broker.send(ContextVariables.CANVAS_DISPOSED_ITEM,
				new CanvasEvent<IProcessorItem>(item, null));
	}
}
