package cz.martinbayer.e4.analyser.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

public class NewProjectHandler {
	@Execute
	public void execute() {

	}

	@CanExecute
	public boolean canExecute() {
		// TODO Your code goes here
		return true;
	}

}