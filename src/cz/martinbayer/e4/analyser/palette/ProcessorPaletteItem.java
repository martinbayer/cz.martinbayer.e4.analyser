package cz.martinbayer.e4.analyser.palette;

import java.io.Serializable;

import org.eclipse.jface.resource.ImageDescriptor;

import cz.martinbayer.analyser.processors.IProcessorItemWrapper;
import cz.martinbayer.analyser.processors.IProcessorLogic;
import cz.martinbayer.analyser.processors.IProcessorsPaletteItem;
import cz.martinbayer.analyser.processors.model.IE4LogsisLog;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;

/**
 * represents processor as node for the treeviewer
 * 
 * @author Martin
 * 
 */
public class ProcessorPaletteItem implements SubPaletteItem, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -301043338414674093L;
	private IProcessorsPaletteItem item;
	private IProcessorLogic<IE4LogsisLog> logic;
	private transient RootPaletteItem parent;
	private IProcessorItemWrapper<IE4LogsisLog> itemWrapper;

	public ProcessorPaletteItem(IProcessorItemWrapper<IE4LogsisLog> itemWrapper) {
		this.itemWrapper = itemWrapper;
		this.item = itemWrapper.getProcessorPaletteItem();
		this.logic = itemWrapper.getProcessorLogic();
	}

	@Override
	public String getLabel() {
		return this.item.getLabel();
	}

	@Override
	public ImageDescriptor getImage() {
		return item.getImageDescriptor(CanvasProcessorItem.DEFAULT_SIZE.x,
				CanvasProcessorItem.DEFAULT_SIZE.x);
	}

	@Override
	public ImageDescriptor getDisabledImage() {
		return item.getDisabledImageDescriptor(
				CanvasProcessorItem.DEFAULT_SIZE.x,
				CanvasProcessorItem.DEFAULT_SIZE.x);
	}

	public IProcessorsPaletteItem getItem() {
		return item;
	}

	public void setItem(IProcessorsPaletteItem item) {
		this.item = item;
	}

	public IProcessorLogic<IE4LogsisLog> getLogic() {
		return logic;
	}

	public void setLogic(IProcessorLogic<IE4LogsisLog> logic) {
		this.logic = logic;
	}

	public void setParent(RootPaletteItem parent) {
		this.parent = parent;
	}

	@Override
	public RootPaletteItem getParent() {
		return parent;
	}

	public IProcessorItemWrapper<IE4LogsisLog> getItemWrapper() {
		return itemWrapper;
	}
}
