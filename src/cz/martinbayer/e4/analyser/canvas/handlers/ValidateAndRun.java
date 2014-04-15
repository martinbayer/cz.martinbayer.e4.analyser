package cz.martinbayer.e4.analyser.canvas.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import cz.martinbayer.analyser.processors.exception.ProcessorFailedException;
import cz.martinbayer.analyser.processors.model.IXMLog;
import cz.martinbayer.analyser.processors.types.InputProcessor;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.dialog.ErrorDialogUtil;
import cz.martinbayer.e4.analyser.statusbar.StatusHandler;
import cz.martinbayer.e4.analyser.statusbar.StatusInfo;
import cz.martinbayer.e4.analyser.validation.ScenarioValidator;
import cz.martinbayer.e4.analyser.validation.ValidationStatus;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public class ValidateAndRun {
	@Inject
	private Logger logger;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	@Execute
	public void execute(
			@Optional @Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager manager) {

		ValidationStatus validationResult;
		validationResult = ScenarioValidator.validateInputProcessors(manager);
		if (!checkValidity(validationResult)) {
			StatusHandler.setStatus(new StatusInfo()
					.setStatusMessage(validationResult.getMessage()));
			return;
		}
		IProcessorItem inputProcessor = manager.getInputProcessors().get(0);
		validationResult = ScenarioValidator
				.validateEnabledProcessors(inputProcessor.getItem()
						.getProcessorLogic().getProcessor());
		if (!checkValidity(validationResult)) {
			StatusHandler.setStatus(new StatusInfo()
					.setStatusMessage(validationResult.getMessage()));
			return;
		}
		InputProcessor<IXMLog> processor = (InputProcessor<IXMLog>) manager
				.getInputProcessors().get(0).getItem().getProcessorLogic()
				.getProcessor();
		try {
			processor.run();
		} catch (ProcessorFailedException e) {
			ErrorDialogUtil.showErrorDialog(shell, e);
			logger.error("Error ocured during processing", e);
		}

	}

	private boolean checkValidity(ValidationStatus validationResult) {
		if (!validationResult.isValid()) {
			StatusHandler.setStatus(new StatusInfo()
					.setStatusMessage(validationResult.getMessage()));
			return false;
		}
		return true;
	}

	@CanExecute
	public boolean canExecute(
			MPart part,
			@Optional @Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager manager) {
		return manager != null && manager.getInputProcessors().size() > 0;
	}

}