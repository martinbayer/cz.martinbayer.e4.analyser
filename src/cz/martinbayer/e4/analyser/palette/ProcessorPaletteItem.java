package cz.martinbayer.e4.analyser.palette;

import org.eclipse.jface.resource.ImageDescriptor;

import cz.martinbayer.analyser.processors.IProcessorLogic;
import cz.martinbayer.analyser.processors.IProcessorsPaletteItem;
import cz.martinbayer.analyser.processors.model.IXMLog;

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

	public ProcessorPaletteItem(IProcessorsPaletteItem item,
			IProcessorLogic<IXMLog> logic) {
		this.item = item;
		this.logic = logic;
	}

	@Override
	public String getLabel() {
		return logic.getProcessor().getName();
	}

	@Override
	public ImageDescriptor getImage() {
		return item.getImageDescriptor();
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
}
