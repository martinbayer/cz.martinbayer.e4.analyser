package cz.martinbayer.e4.analyser.handlers;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.EMenuService;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.persistence.ActualFileHelper;
import cz.martinbayer.e4.analyser.persistence.SerializationHelper;
import cz.martinbayer.e4.analyser.statusbar.StatusHandler;
import cz.martinbayer.e4.analyser.statusbar.StatusInfo;
import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public class SaveProjectHandler {

	public static final String SAVE_PROJECT_COMMAND = "cz.martinbayer.e4.analyser.command.saveproject";
	@Inject
	private Logger l;

	@Execute
	public void execute(
			IEclipseContext eclipseContext,
			EMenuService service,
			@Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager canvasManager) {

		File actualFile = ActualFileHelper.getActualFile(eclipseContext);
		if (actualFile == null) {
			/* no actual file in use so open Save As dialog instead */
			ECommandService commandService = eclipseContext
					.get(ECommandService.class);
			EHandlerService handlerService = eclipseContext
					.get(EHandlerService.class);

			ParameterizedCommand myCommand = commandService.createCommand(
					SaveAsProjectHandler.SAVE_AS_PROJECT_COMMAND, null);
			handlerService.executeHandler(myCommand);
			return;
		}

		List<IProcessorItem> processors = canvasManager.getProcessors();
		List<ILine> lines = canvasManager.getLines();
		StatusInfo info = SerializationHelper.saveProject(actualFile,
				processors, lines);
		if (info != null) {
			StatusHandler.setStatus(info);
			l.error("Status returned while saving project:"
					+ info.getStatusMessage());

		} else {
			l.info("Project succesfully saved.");
		}

	}

	@CanExecute
	public boolean canExecute(
			@Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager canvasManager) {
		return canvasManager.getProcessors().size() > 0
				|| canvasManager.getLines().size() > 0;
	}

}