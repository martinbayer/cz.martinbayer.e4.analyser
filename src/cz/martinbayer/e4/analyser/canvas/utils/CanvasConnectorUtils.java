package cz.martinbayer.e4.analyser.canvas.utils;

import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;

public class CanvasConnectorUtils {

	/**
	 * Check
	 * 
	 * @param source1
	 * @param destination1
	 * @param source2
	 * @param destination2
	 * @return
	 */
	public static boolean areConnectionsSame(CanvasProcessorItem source1,
			CanvasProcessorItem destination1, CanvasProcessorItem source2,
			CanvasProcessorItem destination2) {
		if (source1 != null && destination1 != null) {
			if (source2 == null || destination2 == null) {
				return false;
			} else {
				return (source1.equals(source2) || source1.equals(destination2))
						&& (destination1.equals(source2) || destination1
								.equals(destination2));
			}
		}
		if (source1 != null) {
			if (source2 == null ^ destination2 == null) {
				return source1.equals(source2) || source1.equals(destination2);
			} else {
				return false;
			}
		} else if (destination1 != null) {
			if (source2 == null ^ destination2 == null) {
				return destination1.equals(source2)
						|| destination1.equals(destination2);
			} else {
				return false;
			}
		} else {
			return source2 == null && destination2 == null;
		}
	}
}
