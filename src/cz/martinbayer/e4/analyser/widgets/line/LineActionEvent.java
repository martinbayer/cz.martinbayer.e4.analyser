package cz.martinbayer.e4.analyser.widgets.line;

import java.util.EventObject;

import org.eclipse.swt.events.TypedEvent;

public class LineActionEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 131307018515171915L;
	private Object data;
	private LineEventType eventType;
	private TypedEvent originalEvent;

	public LineActionEvent(ILine source, LineEventType eventType,
			TypedEvent originalEvent, Object data) {
		super(source);
		this.data = data;
		this.eventType = eventType;
		this.originalEvent = originalEvent;
	}

	/**
	 * I can use only ILine instance in constructor so it can be casted here to
	 * avoid lots of casting later
	 */
	@Override
	public ILine getSource() {
		return (ILine) super.source;
	}

	public Object getData() {
		return data;
	}

	public LineEventType getEventType() {
		return eventType;
	}

	public TypedEvent getOriginalEvent() {
		return originalEvent;
	}
}
