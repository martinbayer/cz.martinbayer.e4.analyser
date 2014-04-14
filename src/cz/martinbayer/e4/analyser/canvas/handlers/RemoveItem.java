package cz.martinbayer.e4.analyser.canvas.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public class RemoveItem {
	private Logger logger = LoggerFactory.getInstance(getClass());

	@Execute
	public void execute(
			@Optional @Named(value = ContextVariables.CANVAS_PROCESSOR_SELECTED) CanvasEvent<IProcessorItem> processorEvent,
			@Optional @Named(value = ContextVariables.CANVAS_CONNECTION_SELECTED) CanvasEvent<ILine> lineEvent) {
		if (processorEvent != null && processorEvent.getItem() != null) {
			processorEvent.getItem().remove();
		} else if (lineEvent != null && lineEvent.getItem() != null) {
			lineEvent.getItem().remove();
		} else {
			logger.debug("No processor nor line selected to be removed");
		}
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}

}