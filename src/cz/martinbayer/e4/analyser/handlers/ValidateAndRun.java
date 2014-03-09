package cz.martinbayer.e4.analyser.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class ValidateAndRun {

	public ValidateAndRun() {
		System.out.println("sass");
	}

	@Execute
	public void execute(EPartService partService/*
												 * ,
												 * 
												 * @Optional @Named(value =
												 * ContextVariables
												 * .CANVAS_INPUT_ITEMS)
												 * ArrayList
												 * <LogProcessor<IXMLog>>
												 * inputProcessors
												 */) {
		// if (inputProcessors != null) {
		// System.out.println(inputProcessors.size());
		// } else {
		// System.out.println("asdasd");
		// }
		System.out.println("asdoiahsdoiahsdioahsdo");
	}

	@CanExecute
	public boolean canExecute(MPart part) {
		return true;
	}

}