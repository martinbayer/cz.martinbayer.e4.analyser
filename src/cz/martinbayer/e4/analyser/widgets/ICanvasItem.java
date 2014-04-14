package cz.martinbayer.e4.analyser.widgets;

import java.util.UUID;

import org.eclipse.e4.ui.services.EMenuService;

public interface ICanvasItem {

	void itemTaken(Object param);

	void itemUntaken(Object param);

	void itemHovered(Object param);

	void itemSelected(Object param);

	void itemUnselected(Object param);

	void setSelected(boolean selected, Object param);

	boolean remove();

	UUID getItemId();

	/**
	 * @Deprecated only temporary method. It is used to initialize menu service
	 *             for deserialized items because menu service is not available
	 *             in the handler @Execute method
	 */
	void reinitMenu(EMenuService menuService);
}
