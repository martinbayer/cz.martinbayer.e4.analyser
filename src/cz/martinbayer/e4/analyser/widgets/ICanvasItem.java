package cz.martinbayer.e4.analyser.widgets;

import java.util.UUID;

public interface ICanvasItem {

	void itemTaken(Object param);

	void itemUntaken(Object param);

	void itemHovered(Object param);

	void itemSelected(Object param);

	void itemUnselected(Object param);

	void setSelected(boolean selected, Object param);

	boolean remove();

	UUID getItemId();
}
