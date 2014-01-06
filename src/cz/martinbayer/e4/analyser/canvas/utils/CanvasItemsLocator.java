package cz.martinbayer.e4.analyser.canvas.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItem;

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

	public static boolean isItemOverlapping(List<CanvasItem> items, int x,
			int y, int width, int height) {
		Rectangle newItemBounds = new Rectangle(x - MIN_DISTANCE, y
				- MIN_DISTANCE, width + 2 * MIN_DISTANCE, height + 2
				* MIN_DISTANCE);
		for (CanvasItem item : items) {
			if (item.getBounds().intersects(newItemBounds)) {
				return true;
			}
		}
		return false;
	}

	public static List<CanvasItem> getItemsOverlapping(List<CanvasItem> items,
			int x, int y, int width, int height) {
		List<CanvasItem> overlappingItems = new ArrayList<>();
		Rectangle newItemBounds = getExtendedItemBounds(x, y, width, height);
		for (CanvasItem item : items) {
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
	 * @param items
	 */
	public static void normalizeLocations(List<CanvasItem> items) {
		int minX = 0;
		int minY = 0;
		int actItemX, actItemY;
		for (CanvasItem item : items) {
			actItemX = item.getLocation().x;
			actItemY = item.getLocation().y;
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
			for (CanvasItem item : items) {
				item.move(moveX, moveY);
			}
		}
	}

	/** TODO - will be implemented in case there is enough time */
	public static Point fixDistances(Point actItemNewPosition,
			CanvasItem actItem, List<CanvasItem> items) {
		int x = actItemNewPosition.x;
		int y = actItemNewPosition.y;
		int width = actItem.getBounds().width;
		int height = actItem.getBounds().height;
		// items without actualItem
		List<CanvasItem> workingItems = new ArrayList<>(items);
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
