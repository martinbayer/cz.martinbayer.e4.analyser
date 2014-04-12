package cz.martinbayer.e4.analyser.widgets.processoritem;

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.swt.graphics.Point;

import cz.martinbayer.analyser.processors.IProcessorItemWrapper;
import cz.martinbayer.analyser.processors.model.IXMLog;
import cz.martinbayer.e4.analyser.palette.ProcessorPaletteItem;

public class SerializableCanvasProcessorItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5211190179682333417L;

	private ProcessorPaletteItem origPaletteItem;

	private IProcessorItemWrapper<IXMLog> processorItem;

	private Point location;

	private UUID processorId;

	public SerializableCanvasProcessorItem(IProcessorItem item) {
		this.origPaletteItem = item.getOrigPaletteItem();
		this.processorItem = item.getItem();
		this.location = ((CanvasProcessorItem) item).getLocation();
		this.processorId = item.getItemId();
		resetData(item);
	}

	private void resetData(IProcessorItem item) {
		/*
		 * remove processors because they are recreated during deserialization
		 * so duplicates would occur
		 */
		item.getItem().getProcessorLogic().getProcessor().removeProcessor(null);
	}

	public final ProcessorPaletteItem getOrigPaletteItem() {
		return origPaletteItem;
	}

	public final IProcessorItemWrapper<IXMLog> getProcessorItem() {
		return processorItem;
	}

	public Point getLocation() {
		return this.location;
	}

	public UUID getProcessorId() {
		return this.processorId;
	}
}