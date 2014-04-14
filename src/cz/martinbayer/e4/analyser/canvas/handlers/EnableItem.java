package cz.martinbayer.e4.analyser.canvas.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public class EnableItem {
	private Logger logger = LoggerFactory.getInstance(getClass());

	@Execute
	public void execute(
			@Optional @Named(value = ContextVariables.CANVAS_PROCESSOR_SELECTED) CanvasEvent<IProcessorItem> processorEvent) {
		if (processorEvent != null && processorEvent.getItem() != null) {
			processorEvent.getItem().getItem().getProcessorLogic()
					.getProcessor().setEnabled(true);
		} else {
			logger.debug("No processor selected to be enabled");
		}
	}

	/**
	 * Allow the action if the item is disabled
	 * 
	 * @param processorEvent
	 * @return
	 */
	@CanExecute
	public boolean canExecute(
			@Optional @Named(value = ContextVariables.CANVAS_PROCESSOR_SELECTED) CanvasEvent<IProcessorItem> processorEvent) {
		if (processorEvent != null && processorEvent.getItem() != null) {
			boolean enabled = processorEvent.getItem().getItem()
					.getProcessorLogic().getProcessor().isEnabled();
			return !enabled;
		}
		return false;
	}
}
