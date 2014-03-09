package cz.martinbayer.e4.analyser.canvas;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.widgets.line.ILineEventListener;
import cz.martinbayer.e4.analyser.widgets.line.LineActionEvent;
import cz.martinbayer.e4.analyser.widgets.line.LineEventType;

public class CanvasLineEventListener implements ILineEventListener {

	private IEclipseContext context;
	private IEventBroker broker;

	public CanvasLineEventListener(IEclipseContext context) {
		this.context = context;
		broker = this.context.get(IEventBroker.class);
	}

	@Override
	public void handleMouseDown(LineActionEvent event) {
		if (event.getEventType().equals(LineEventType.START_POINT_CLICKED)
				|| event.getEventType().equals(LineEventType.END_POINT_CLICKED)) {
			this.broker.send(ContextVariables.CANVAS_CONNECTION_SELECTED,
					event.getSource());
			this.broker.send(ContextVariables.CANVAS_CONNECTION_TAKEN,
					event.getSource());
		}

	}

	@Override
	public void handleMouseUp(LineActionEvent event) {
		if (event.getEventType().equals(LineEventType.START_POINT_CLICKED)
				|| event.getEventType().equals(LineEventType.END_POINT_CLICKED)) {
			this.broker.send(ContextVariables.CANVAS_CONNECTION_SELECTED, null);
		}
	}

}
