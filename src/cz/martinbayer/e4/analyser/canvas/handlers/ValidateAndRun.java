package cz.martinbayer.e4.analyser.canvas.handlers;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import cz.martinbayer.analyser.processors.events.IProcessorStatusListener;
import cz.martinbayer.analyser.processors.exception.ProcessorFailedException;
import cz.martinbayer.analyser.processors.model.IE4LogsisLog;
import cz.martinbayer.analyser.processors.types.InputProcessor;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.dialog.ErrorDialogUtil;
import cz.martinbayer.e4.analyser.statusbar.ProcessingStatusListener;
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
			MApplication application,
			@Optional @Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager manager,
			@Active MWindow window) {
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
		IProcessorItem inputProcItem = manager.getInputProcessors().get(0);
		InputProcessor<IE4LogsisLog> processor = (InputProcessor<IE4LogsisLog>) inputProcItem
				.getItem().getProcessorLogic().getProcessor();
		runProcessing(manager, inputProcItem, processor, application, window,
				shell);

	}

	private void runProcessing(final ICanvasManager manager,
			final IProcessorItem inputProcItem,
			final InputProcessor<IE4LogsisLog> processor,
			final MApplication application, final MWindow window, Shell shell) {
		final IRunnableWithProgress op = new IRunnableWithProgress() {

			private boolean complete = false;
			private Object lock = new Object();

			private boolean isComplete() {
				synchronized (lock) {
					return complete;
				}
			}

			private void setComplete(boolean complete) {
				synchronized (lock) {
					this.complete = complete;
				}
			}

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Processing in progress...",
						IProgressMonitor.UNKNOWN);
				startProcess(monitor);
				System.out.println("Process started:"
						+ new SimpleDateFormat("HH:mm:ss.SSS")
								.format(new Date()));
				while (!isComplete()) {
					Thread.sleep(2000);
					if (monitor.isCanceled()) {
						/*
						 * clear data as the process was not successfully
						 * completed
						 */
						application.getContext().set(
								ContextVariables.PROCESSING_CANCELED, true);
						processor.cancel();
						break;
					}
				}
				if (!monitor.isCanceled()) {
					application.getContext().set(
							ContextVariables.PROCESSING_CANCELED, false);

				}
				monitor.done();
				System.out.println("Process finished:"
						+ new SimpleDateFormat("HH:mm:ss.SSS")
								.format(new Date()));
			}

			private void startProcess(final IProgressMonitor monitor) {
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						runWithProgress(manager, inputProcItem, processor,
								application, window, monitor);
						setComplete(true);
					}

				}, "dataprocessing");
				t.start();

			}
		};
		final ProgressMonitorDialog progDialog = new ProgressMonitorDialog(
				shell);
		try {
			progDialog.run(true, true, op);
			String message = "";
			if (progDialog.getProgressMonitor().isCanceled()) {
				message = "Process was canceled by user. Data must be read again.";
				MessageDialog.openInformation(shell, "Export result", message);
			}

		} catch (InvocationTargetException | InterruptedException e1) {
			e1.printStackTrace();

			MessageDialog.openError(null, "Export failed",
					"Check logs for more information");

		}
	}

	private void runWithProgress(ICanvasManager manager,
			IProcessorItem inputProcItem,
			InputProcessor<IE4LogsisLog> processor, MApplication application,
			MWindow window, IProgressMonitor monitor) {
		try {
			/*
			 * there can be data saved in collection for other input processors
			 * so clear them all to avoid memory issues
			 */
			manager.clearDataForInputProcsBut(inputProcItem);

			IProcessorStatusListener listener = new ProcessingStatusListener(
					monitor);
			processor.setStatusListener(listener);
			boolean usePreviousData = manager.usePreviousData(application,
					window);
			/*
			 * indicates whether processing was canceled previously, if so, then
			 * data will be read again
			 */
			boolean processWasCanceled = false;
			if (application.getContext().containsKey(
					ContextVariables.PROCESSING_CANCELED)) {
				processWasCanceled = (boolean) application.getContext().get(
						ContextVariables.PROCESSING_CANCELED);
			}
			processor.run(usePreviousData && !processWasCanceled);
		} catch (ProcessorFailedException e) {
			ErrorDialogUtil.showErrorDialog(shell, e);
			logger.error(e, "Error ocured during processing");
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