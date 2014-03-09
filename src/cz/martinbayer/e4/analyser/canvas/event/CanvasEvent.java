package cz.martinbayer.e4.analyser.canvas.event;

import org.eclipse.swt.events.TypedEvent;

import cz.martinbayer.e4.analyser.widgets.ICanvasItem;

public class CanvasEvent<T extends ICanvasItem> {

	private TypedEvent origEvent;
	private T item;

	public CanvasEvent(T item, TypedEvent origEvent) {
		this.item = item;
		this.origEvent = origEvent;
	}

	public final TypedEvent getOrigEvent() {
		return origEvent;
	}

	public final void setOrigEvent(TypedEvent origEvent) {
		this.origEvent = origEvent;
	}

	public final T getItem() {
		return item;
	}

	public final void setItem(T item) {
		this.item = item;
	}

}
