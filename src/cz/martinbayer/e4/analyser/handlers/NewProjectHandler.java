package cz.martinbayer.e4.analyser.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.persistence.ActualFileHelper;
import cz.martinbayer.e4.analyser.persistence.SerializationHelper;

public class NewProjectHandler {
	@Execute
	public void execute(
			IEclipseContext eclipseContext,
			@Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager canvasManager,
			Shell shell) {
		/*
		 * handle the situation when there is some scenario already opened or in
		 * progress. False is returned if 'Cancel' is pressed on 'Save changes?'
		 * dialog.
		 */
		if (!SerializationHelper.checkActuallProject(canvasManager,
				eclipseContext, shell)) {
			return;
		} else {
			canvasManager.removeAll();
			/*
			 * set actual file to NULL to be forced to choose file on save
			 * action
			 */
			ActualFileHelper.setActualFile(eclipseContext, null);
		}
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}

}