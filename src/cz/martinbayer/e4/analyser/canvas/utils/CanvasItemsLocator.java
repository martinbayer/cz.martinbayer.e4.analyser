package cz.martinbayer.e4.analyser.canvas.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

/**
 * Contains logarithms to place the items to be always at least VALUEpx apart
 * from each other
 * 
 * @author Martin
 * 
 */
public class CanvasItemsLocator {

	enum CanvasItemLocation {
		LEFT, RIGHT, TOP, BOTTOM
	}

	private static final int MIN_DISTANCE = 10;

	public static boolean isItemOverlapping(List<CanvasProcessorItem> items,
			int x, int y, int width, int height) {
		Rectangle newItemBounds = new Rectangle(x - MIN_DISTANCE, y
				- MIN_DISTANCE, width + 2 * MIN_DISTANCE, height + 2
				* MIN_DISTANCE);
		for (CanvasProcessorItem item : items) {
			if (item.getBounds().intersects(newItemBounds)) {
				return true;
			}
		}
		return false;
	}

	public static List<CanvasProcessorItem> getItemsOverlapping(
			List<CanvasProcessorItem> items, int x, int y, int width, int height) {
		List<CanvasProcessorItem> overlappingItems = new ArrayList<>();
		Rectangle newItemBounds = getExtendedItemBounds(x, y, width, height);
		for (CanvasProcessorItem item : items) {
			if (item.getBounds().intersects(newItemBounds)) {
				overlappingItems.add(item);
			}
		}
		return overlappingItems;
	}

	private static Rectangle getExtendedItemBounds(int x, int y, int width,
			int height) {
		Rectangle extendItemBounds = new Rectangle(x - MIN_DISTANCE, y
				- MIN_DISTANCE, width + 2 * MIN_DISTANCE, height + 2
				* MIN_DISTANCE);
		return extendItemBounds;
	}

	/**
	 * Relocates all items to be placed minimally on (x=0,y=0)
	 * 
	 * @param list
	 */
	public static void normalizeLocations(List<IProcessorItem> list) {
		int minX = 0;
		int minY = 0;
		int actItemX, actItemY;
		for (IProcessorItem item : list) {
			actItemX = ((CanvasProcessorItem) item).getLocation().x;
			actItemY = ((CanvasProcessorItem) item).getLocation().y;
			if (actItemX < minX) {
				minX = actItemX;
			}
			if (actItemY < minY) {
				minY = actItemY;
			}
		}
		if (minX < 0 || minY < 0) {
			int moveX = Math.abs(minX);
			int moveY = Math.abs(minY);
			for (IProcessorItem item : list) {
				((CanvasProcessorItem) item).move(moveX, moveY);
			}
		}
	}

	/** TODO - will be implemented in case there is enough time */
	public static Point fixDistances(Point actItemNewPosition,
			CanvasProcessorItem actItem, List<CanvasProcessorItem> items) {
		int x = actItemNewPosition.x;
		int y = actItemNewPosition.y;
		int width = actItem.getBounds().width;
		int height = actItem.getBounds().height;
		// items without actualItem
		List<CanvasProcessorItem> workingItems = new ArrayList<>(items);
		workingItems.remove(actItem);
		if (!isItemOverlapping(workingItems, x, y, width, height)) {
			return new Point(x, y);
		}
		workingItems = getItemsOverlapping(workingItems, x, y, width, height);
		// co kdyz se dostane jedna itema mezi 3 nebo 4 dalsi, treba i kruhove
		// poskladane...je treba nacmarat
		return null;
	}
}
