package cz.martinbayer.e4.analyser.palette;

import org.eclipse.jface.resource.ImageDescriptor;

import cz.martinbayer.e4.analyser.description.gui.ProcessorsPaletteItem;
import cz.martinbayer.e4.analyser.description.logic.ProcessorLogic;

/**
 * represents processor as node for the treeviewer
 * 
 * @author Martin
 * 
 */
public class ProcessorPaletteItem implements SubPaletteItem {

	private ProcessorsPaletteItem item;
	private ProcessorLogic logic;
	private RootPaletteItem parent;

	public ProcessorPaletteItem(ProcessorsPaletteItem item, ProcessorLogic logic) {
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

	public ProcessorsPaletteItem getItem() {
		return item;
	}

	public void setItem(ProcessorsPaletteItem item) {
		this.item = item;
	}

	public ProcessorLogic getLogic() {
		return logic;
	}

	public void setLogic(ProcessorLogic logic) {
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
