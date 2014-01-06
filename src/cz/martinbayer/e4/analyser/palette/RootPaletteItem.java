package cz.martinbayer.e4.analyser.palette;

import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import cz.martinbayer.utils.ImageUtils;

public class RootPaletteItem implements PaletteItem {
	private ArrayList<PaletteItem> children;
	private ImageDescriptor image;
	private String label;

	/**
	 * Constructor is used to create root for processors of one type
	 * 
	 * @param label
	 * @param iconName
	 *            - must contains extension and must be placed in "icons"
	 *            directory
	 */
	public RootPaletteItem(String label, String iconName) {
		this.label = label;
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		this.image = ImageUtils.getImage("icons/".concat(iconName), bundle, 20,
				20);
		this.children = new ArrayList<>();
	}

	public ArrayList<PaletteItem> getChildren() {
		return children;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public ImageDescriptor getImage() {
		return image;
	}

	public void addChild(PaletteItem child) {
		this.children.add(child);
	}
}
