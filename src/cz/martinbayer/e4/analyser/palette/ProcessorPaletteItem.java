package cz.martinbayer.e4.analyser.palette;

import org.eclipse.jface.resource.ImageDescriptor;

import cz.martinbayer.analyser.processors.IProcessorItemWrapper;
import cz.martinbayer.analyser.processors.IProcessorLogic;
import cz.martinbayer.analyser.processors.IProcessorsPaletteItem;
import cz.martinbayer.analyser.processors.model.IXMLog;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;

/**
 * represents processor as node for the treeviewer
 * 
 * @author Martin
 * 
 */
public class ProcessorPaletteItem implements SubPaletteItem {

	private IProcessorsPaletteItem item;
	private IProcessorLogic<IXMLog> logic;
	private RootPaletteItem parent;
	private IProcessorItemWrapper<IXMLog> itemWrapper;

	public ProcessorPaletteItem(IProcessorItemWrapper<IXMLog> itemWrapper) {
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

	public IProcessorLogic<IXMLog> getLogic() {
		return logic;
	}

	public void setLogic(IProcessorLogic<IXMLog> logic) {
		this.logic = logic;
	}

	public void setParent(RootPaletteItem parent) {
		this.parent = parent;
	}

	@Override
	public RootPaletteItem getParent() {
		return parent;
	}

	public IProcessorItemWrapper<IXMLog> getItemWrapper() {
		return itemWrapper;
	}
}
