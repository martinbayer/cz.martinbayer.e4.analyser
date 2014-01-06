package cz.martinbayer.e4.analyser.palette;

public class OtherPaletteItems extends RootPaletteItem {

	public static final String label = "other_item";
	public static final String icon = "other_item.png";

	public OtherPaletteItems() {
		super(label, icon);
		createConnectionItem(this);
	}

	private void createConnectionItem(RootPaletteItem root) {
		ConnectionPaletteItem connectionItem = new ConnectionPaletteItem();
		root.addChild(connectionItem);
	}
}
