package cz.martinbayer.e4.analyser.canvas.event;

import org.eclipse.swt.events.TypedEvent;

import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.line.LinePart;

public class LineEvent<T extends ILine> extends CanvasEvent<T> {

	private LinePart linePart;

	public LineEvent(T line, TypedEvent origEvent, LinePart linePart) {
		super(line, origEvent);
		this.linePart = linePart;
	}

	public final LinePart getLinePart() {
		return linePart;
	}
}
