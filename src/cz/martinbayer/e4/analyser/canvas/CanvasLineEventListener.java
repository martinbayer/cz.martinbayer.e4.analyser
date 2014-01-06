package cz.martinbayer.e4.analyser.canvas;

import cz.martinbayer.e4.analyser.widgets.line.ILineEventListener;
import cz.martinbayer.e4.analyser.widgets.line.LineActionEvent;
import cz.martinbayer.e4.analyser.widgets.line.LineEventType;

public class CanvasLineEventListener implements ILineEventListener {

	private ICanvas canvas;

	public CanvasLineEventListener(ICanvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void handleMouseDown(LineActionEvent event) {
		if (event.getEventType().equals(LineEventType.START_POINT_CLICKED)
				|| event.getEventType().equals(LineEventType.END_POINT_CLICKED)) {
			this.canvas.setSelectedConnection(event.getSource());
		}

	}

	@Override
	public void handleMouseUp(LineActionEvent event) {
		if (event.getEventType().equals(LineEventType.START_POINT_CLICKED)
				|| event.getEventType().equals(LineEventType.END_POINT_CLICKED)) {
			this.canvas.setSelectedConnection(null);
		}
	}

}
