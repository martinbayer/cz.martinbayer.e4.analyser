package cz.martinbayer.e4.analyser.widgets.processoritem.actions;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public class Delete {
	@Execute
	public void execute(
			@Named(value = ContextVariables.CANVAS_PROCESSOR_SELECTED) CanvasEvent<IProcessorItem> event) {
		if (event != null) {
			((CanvasProcessorItem) event.getItem()).dispose();
		}
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}

}