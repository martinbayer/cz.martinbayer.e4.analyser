package cz.martinbayer.e4.analyser.palette;

import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import cz.martinbayer.utils.ImageUtils;

public class ConnectionPaletteItem implements PaletteItem {

	private String label = "Connection";
	private String icon = "connection_item";

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public ImageDescriptor getImage() {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		StringBuilder path = new StringBuilder("icons/");
		path.append(icon);
		path.append(ImageUtils.PNG_EXTENSION);
		return ImageUtils.getImage(path.toString(), bundle, 20, 20);
	}

	@Override
	public ImageDescriptor getDisabledImage() {
		throw new UnsupportedOperationException("Connection cannot be disabled");
	}
}
