package cz.martinbayer.e4.analyser.widgets.processoritem;

import cz.martinbayer.analyser.processors.IProcessorItemWrapper;
import cz.martinbayer.analyser.processors.model.IXMLog;
import cz.martinbayer.e4.analyser.widgets.ICanvasItem;
import cz.martinbayer.e4.analyser.widgets.line.connection.ItemConnectionConnector;

public interface IProcessorItem extends ICanvasItem {

	boolean addConnection(ItemConnectionConnector connector);

	IProcessorItemWrapper<IXMLog> getItem();

	void setTaken(boolean taken);

	void setSelected(boolean selected);

	void setHovered(boolean hovered);
}
