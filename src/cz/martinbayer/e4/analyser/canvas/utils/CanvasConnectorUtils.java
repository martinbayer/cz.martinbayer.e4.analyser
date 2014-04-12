package cz.martinbayer.e4.analyser.canvas.utils;

import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

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
	public static boolean areConnectionsSame(IProcessorItem source1,
			IProcessorItem destination1, IProcessorItem source2,
			IProcessorItem destination2) {
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
				return source1.equals(source2);
			}
		} else if (destination1 != null) {
			if (source2 == null ^ destination2 == null) {
				return destination1.equals(source2)
						|| destination1.equals(destination2);
			} else {
				return destination1.equals(destination2);
			}
		} else {
			return source2 == null && destination2 == null;
		}
	}
}
