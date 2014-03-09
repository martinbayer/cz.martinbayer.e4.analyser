package cz.martinbayer.e4.analyser.widgets.processoritem;

import cz.martinbayer.e4.analyser.palette.ProcessorPaletteItem;
import cz.martinbayer.e4.analyser.widgets.ICanvasItem;
import cz.martinbayer.e4.analyser.widgets.line.connection.ItemConnectionConnector;

public interface IProcessorItem extends ICanvasItem {

	boolean addConnection(ItemConnectionConnector connector);

	ProcessorPaletteItem getItem();

	void setTaken(boolean taken);

	void setSelected(boolean selected);

	void setHovered(boolean hovered);
}
