package cz.martinbayer.e4.analyser.validation;

import java.util.ArrayList;
import java.util.List;

import cz.martinbayer.analyser.processors.model.IE4LogsisLog;
import cz.martinbayer.analyser.processors.types.LogProcessor;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public class ScenarioValidator {

	/**
	 * Only one enabled input processor can be used in the scenario.
	 * 
	 * @param manager
	 *            - handles all objects on the canvas
	 * @return -
	 */
	public static ValidationStatus validateInputProcessors(
			ICanvasManager manager) {
		List<IProcessorItem> enabledInputProcs = new ArrayList<>();
		for (IProcessorItem item : manager.getInputProcessors()) {
			if (item.isItemEnabled()) {
				enabledInputProcs.add(item);
			}
		}
		if (enabledInputProcs.size() < 1) {
			return new ValidationStatus(false,
					"At least one input processor must be used to run the process");
		} else if (enabledInputProcs.size() > 1) {
			return new ValidationStatus(
					false,
					"Only one input processor can be enabled. Disable some of the following processors:\n"
							.concat(LogProcessor.getProcessorNames(
									CanvasProcessorItem
											.getProcessors(enabledInputProcs))
									.toString()));
		}
		return new ValidationStatus(true,
				String.format("Input processor %s is used", enabledInputProcs
						.get(0).getItem().getProcessorLogic().getProcessor()
						.getName()));
	}

	public static ValidationStatus validateEnabledProcessors(
			LogProcessor<IE4LogsisLog> processor) {
		StringBuffer sb = processor.isValid();
		if (sb == null || sb.length() == 0) {
			ValidationStatus childProcsStatus;
			for (LogProcessor<IE4LogsisLog> proc : processor.getEnabledProcs()) {
				childProcsStatus = validateEnabledProcessors(proc);
				if (!childProcsStatus.isValid()) {
					return childProcsStatus;
				}
			}
			return new ValidationStatus(true, String.format(
					"Processor %s is valid", processor.getName()));
		} else {
			return new ValidationStatus(false, sb.toString());
		}
	}
}
