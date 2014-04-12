package cz.martinbayer.e4.analyser.widgets.line.connection;

import cz.martinbayer.e4.analyser.widgets.line.LinePart;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public class ItemConnectionConnector {

	private LinePart part;
	private IProcessorItem item;
	private ConnectionItem connection;

	public ItemConnectionConnector(ConnectionItem connection,
			IProcessorItem item, LinePart part) {
		assert part != LinePart.LINE : "Only spot can be used as connector";
		this.connection = connection;
		this.item = item;
		this.part = part;
	}

	public LinePart getPart() {
		return part;
	}

	public IProcessorItem getItem() {
		return item;
	}

	public ConnectionItem getConnection() {
		return connection;
	}

	public void move(int moveX, int moveY) {
		connection.move(moveX, moveY, part);
	}
}
