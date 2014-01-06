package cz.martinbayer.e4.analyser.palette;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ProcessorsLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		String label = ((PaletteItem) element).getLabel();
		return label;
	}

	@Override
	public Image getImage(Object element) {
		ImageDescriptor imgDesc = null;
		imgDesc = ((PaletteItem) element).getImage();
		if (imgDesc != null) {
			return imgDesc.createImage();
		}
		return null;
	}
}
