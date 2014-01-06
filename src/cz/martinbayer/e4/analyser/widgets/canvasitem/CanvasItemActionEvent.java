package cz.martinbayer.e4.analyser.widgets.canvasitem;

import java.util.EventObject;

import org.eclipse.swt.events.TypedEvent;

public class CanvasItemActionEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1665323505886256801L;

	private Object data;
	private TypedEvent originalEvent;

	public CanvasItemActionEvent(ICanvasItem source, Object data,
			TypedEvent originalEvent) {
		super(source);
		this.data = data;
		this.originalEvent = originalEvent;
	}

	@Override
	public ICanvasItem getSource() {
		return (ICanvasItem) super.getSource();
	}

	public Object getData() {
		return data;
	}

	public TypedEvent getOriginalEvent() {
		return originalEvent;
	}

}
