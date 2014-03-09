package cz.martinbayer.e4.analyser.widgets.processoritem;

import java.io.Serializable;

import org.eclipse.swt.graphics.Point;

public class CanvasItemDnDData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6734552703376916198L;
	private Point sourcePoint;

	public Point getSourcePoint() {
		return sourcePoint;
	}

	public void setSourcePoint(Point sourcePoint) {
		this.sourcePoint = sourcePoint;
	}

}
