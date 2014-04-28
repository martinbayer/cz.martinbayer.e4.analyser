package cz.martinbayer.e4.analyser.statusbar;

import org.eclipse.core.runtime.IProgressMonitor;

import cz.martinbayer.analyser.processors.events.IProcessorStatusListener;
import cz.martinbayer.analyser.processors.events.StatusEvent;

public class ProcessingStatusListener implements IProcessorStatusListener {
	private IProgressMonitor monitor;

	public ProcessingStatusListener(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void stateSubmitted(StatusEvent statusEvent) {
		if (this.monitor != null) {
			this.monitor.subTask(statusEvent.getAsString());
		}
	}

}
