package cz.martinbayer.e4.analyser.canvas.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import cz.martinbayer.analyser.processors.model.IXMLog;
import cz.martinbayer.analyser.processors.types.InputProcessor;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;

public class ValidateAndRun {

	public ValidateAndRun() {
		System.out.println("sass");
	}

	@Execute
	public void execute(
			@Optional @Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager manager) {

		InputProcessor<IXMLog> processor = (InputProcessor<IXMLog>) manager
				.getInputProcessors().get(0).getItem().getLogic()
				.getProcessor();
		processor.run();

	}

	@CanExecute
	public boolean canExecute(
			MPart part,
			@Optional @Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager manager) {
		return manager != null && manager.getInputProcessors().size() > 0;
	}

}