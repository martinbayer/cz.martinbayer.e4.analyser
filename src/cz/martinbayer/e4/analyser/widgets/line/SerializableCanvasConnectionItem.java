package cz.martinbayer.e4.analyser.widgets.line;

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.swt.graphics.Point;

import cz.martinbayer.e4.analyser.widgets.line.connection.ConnectionItem;

public class SerializableCanvasConnectionItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3350960162980669766L;
	private Point startPoint, endPoint;
	private UUID connectionId;
	private UUID sourceId;
	private UUID destinationId;

	public SerializableCanvasConnectionItem(ILine connection) {
		this.startPoint = connection.getStartPoint();
		this.endPoint = connection.getEndPoint();
		this.connectionId = connection.getItemId();
		if (connection instanceof ConnectionItem) {
			sourceId = ((ConnectionItem) connection).getSourceItem()
					.getItemId();
			destinationId = ((ConnectionItem) connection).getDestinationItem()
					.getItemId();
		}
	}

	public final Point getStartPoint() {
		return startPoint;
	}

	public final Point getEndPoint() {
		return endPoint;
	}

	public UUID getConnectionId() {
		return this.connectionId;
	}

	public final UUID getSourceId() {
		return sourceId;
	}

	public final UUID getDestinationId() {
		return destinationId;
	}
}