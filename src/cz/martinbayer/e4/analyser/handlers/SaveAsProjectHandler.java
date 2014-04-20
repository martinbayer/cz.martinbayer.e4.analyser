package cz.martinbayer.e4.analyser.handlers;

import java.io.File;
import java.io.IOException;

import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import cz.martinbayer.e4.analyser.ContextDefaultValues;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.persistence.ActualFileHelper;
import cz.martinbayer.e4.analyser.persistence.SerializationHelper;
import cz.martinbayer.utils.StringUtils;

public class SaveAsProjectHandler {

	private static Logger logger = LoggerFactory
			.getInstance(SaveAsProjectHandler.class);
	public static final String SAVE_AS_PROJECT_COMMAND = "cz.martinbayer.e4.analyser.command.saveasproject";

	@Execute
	public void execute(
			@Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager canvasManager,
			@Preference(nodePath = ContextVariables.Property.PROJECT_OPERATION) IEclipsePreferences prefs,
			IEclipseContext eclipseContext, Shell shell) {
		/* open Save As file dialog with initial value as directory */
		String saveAsDialogPath = prefs.get(
				ContextVariables.Property.LAST_SAVE_DIR, "");
		FileDialog saveDialog = new FileDialog(shell, SWT.SINGLE | SWT.SAVE);
		saveDialog.setFilterPath(saveAsDialogPath);
		saveDialog
				.setFilterExtensions(new String[] { ContextDefaultValues.PROJECT_EXTENSION });
		if (saveDialog.open() == null) {
			return;
		}
		String directory = saveDialog.getFilterPath();
		String fileName = saveDialog.getFileName();
		File selectedFile = null;
		try {
			if (StringUtils.isEmtpy(directory)
					|| StringUtils.isEmtpy(fileName)
					|| (!(selectedFile = new File(directory + File.separator
							+ fileName)).exists() && !selectedFile
								.createNewFile())) {
				MessageDialog.openError(shell, "Invalid file",
						"Please, select valid file to save the project.");
				return;
			}
			SerializationHelper.saveProject(selectedFile,
					canvasManager.getProcessors(), canvasManager.getLines());
			/* no exception thrown so set actual file */
			ActualFileHelper.setActualFile(eclipseContext, selectedFile);
		} catch (IOException e) {
			MessageDialog.openError(
					shell,
					"Unable to save project",
					"Unable to save project to file: "
							+ selectedFile.getAbsolutePath());
			logger.error(
					e,
					"Unable to save project to file:"
							+ selectedFile.getAbsolutePath());
		}
	}

	@CanExecute
	public boolean canExecute(
			@Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager canvasManager) {
		return canvasManager.getProcessors().size() > 0
				|| canvasManager.getLines().size() > 0;
	}
}