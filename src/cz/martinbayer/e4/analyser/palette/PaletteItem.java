package cz.martinbayer.e4.analyser.palette;

import org.eclipse.jface.resource.ImageDescriptor;

public interface PaletteItem {

	String getLabel();

	ImageDescriptor getImage();

	ImageDescriptor getDisabledImage();

}
