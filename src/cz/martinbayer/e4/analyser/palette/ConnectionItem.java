package cz.martinbayer.e4.analyser.palette;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItem;
import cz.martinbayer.e4.analyser.widgets.line.Line;

public class ConnectionItem extends Line {

	private CanvasItem sourceItem, destinationItem;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5154122088790476947L;

	// used to get actually edited connection from context
	public static final String ACTIVE_CONNECTION = "cz.martinbayer.active_connection";

	public ConnectionItem(Composite parent, int style) {
		super(parent, style);
	}

	public CanvasItem getSourceItem() {
		return sourceItem;
	}

	public void setSourceItem(CanvasItem sourceItem) {
		this.sourceItem = sourceItem;
	}

	public CanvasItem getDestinationItem() {
		return destinationItem;
	}

	public void setDestinationItem(CanvasItem destinationItem) {
		this.destinationItem = destinationItem;
	}

	public void move(int moveX, int moveY) {
		Point actLocation = getLocation();
		setLocation(actLocation.x + moveX, actLocation.y + moveY);
	}

}
