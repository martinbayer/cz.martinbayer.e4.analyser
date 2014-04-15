package cz.martinbayer.e4.analyser.widgets.line.connection;

import java.util.UUID;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.widgets.line.CanvasConnectionItem;
import cz.martinbayer.e4.analyser.widgets.line.LinePart;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;

public class ConnectionItem extends CanvasConnectionItem {
	private Logger logger = LoggerFactory.getInstance(getClass());

	private CanvasProcessorItem sourceItem, destinationItem;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5154122088790476947L;

	public ConnectionItem(Composite parent, MApplication application,
			EMenuService menuService, UUID connectionId) {
		super(parent, SWT.NONE, application, menuService, connectionId);
	}

	public CanvasProcessorItem getSourceItem() {
		return sourceItem;
	}

	public void setSourceItem(CanvasProcessorItem sourceItem) {
		this.sourceItem = sourceItem;
	}

	public CanvasProcessorItem getDestinationItem() {
		return destinationItem;
	}

	public void setDestinationItem(CanvasProcessorItem destinationItem) {
		this.destinationItem = destinationItem;
	}

	public void move(int moveX, int moveY, LinePart partType) {
		moveLinePart(moveX, moveY, partType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destinationItem == null) ? 0 : destinationItem.hashCode());
		result = prime * result
				+ ((sourceItem == null) ? 0 : sourceItem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionItem other = (ConnectionItem) obj;
		if (destinationItem == null) {
			if (other.destinationItem != null)
				return false;
		} else if (!destinationItem.equals(other.destinationItem)
				&& !destinationItem.equals(other.sourceItem))
			return false;
		if (sourceItem == null) {
			if (other.sourceItem != null)
				return false;
		} else if (!sourceItem.equals(other.sourceItem)
				&& !sourceItem.equals(other.destinationItem))
			return false;
		return true;
	}

	/**
	 * Method will remove the reference of the connection in the oposite
	 * processor which uses the actual connection.
	 * 
	 * @param connector
	 * @param canvasProcessorItem
	 */
	public void removeItemsRefs(ItemConnectionConnector connector,
			CanvasProcessorItem canvasProcessorItem) {
		if (canvasProcessorItem != null) {
			if (!canvasProcessorItem.equals(sourceItem)) {
				sourceItem.removeConnector(connector.getConnection());
				logger.info("Source connector removed");
				return;
			} else {
				destinationItem.removeConnector(connector.getConnection());
				logger.info("Destination connector removed");
			}
		}
	}

	@Override
	public boolean remove() {
		/* remove connection references for possible items */
		if (sourceItem != null) {
			sourceItem.removeConnector(this);
		}
		if (destinationItem != null) {
			destinationItem.removeConnector(this);
		}
		return super.remove();
	}

	@Override
	public String toString() {
		return String.format("Connection Item [%s->%s]", getSourceItem()
				.getItem().getProcessorLogic().getProcessor().getName(),
				getDestinationItem().getItem().getProcessorLogic()
						.getProcessor().getName());
	}
}
