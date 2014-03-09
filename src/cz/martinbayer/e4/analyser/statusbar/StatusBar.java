package cz.martinbayer.e4.analyser.statusbar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.swt.utils.ColorUtils;

public class StatusBar {
	private Logger logger = LoggerFactory.getInstance(getClass());

	protected static final String EMPTY = "";

	/* status bar will be cleaned in 5 seconds by default */
	protected static final long CLEAN_TIMEOUT = 5000;
	private Label actualStatusLabel;

	private Thread cleanerThread;

	@PostConstruct
	public void postConstruct(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, true));
		GridData data = new GridData();
		data.widthHint = 2000;
		data.horizontalAlignment = GridData.FILL_HORIZONTAL;
		actualStatusLabel = new Label(comp, SWT.NONE);
		actualStatusLabel.setForeground(ColorUtils.getColor(SWT.COLOR_RED));
		actualStatusLabel.setLayoutData(data);
	}

	@Optional
	@Inject
	public void setStatus(
			@UIEventTopic(ContextVariables.APP_STATUS) final StatusInfo status,
			final UISynchronize sync) {
		if (cleanerThread != null && cleanerThread.isAlive()) {
			cleanerThread.interrupt();
		}
		actualStatusLabel.setText(status.getStatusMessage());
		actualStatusLabel.getParent().layout();
		/* clean the status after timeout passes */
		if (!status.isDoNotClean()) {
			cleanerThread = new Thread(new CleanerThread(actualStatusLabel,
					sync), "cleaner thread");
			cleanerThread.start();
		}
		logger.debug("Status changed to:{0}", status.getStatusMessage());
	}

	@Focus
	public void setFocus() {
		this.actualStatusLabel.setFocus();
	}
}

class CleanerThread implements Runnable {

	private Label statusLabel;
	private UISynchronize sync;

	public CleanerThread(Label statusLabel, UISynchronize sync) {
		this.statusLabel = statusLabel;
		this.sync = sync;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(StatusBar.CLEAN_TIMEOUT);
			sync.asyncExec(new Runnable() {
				@Override
				public void run() {
					statusLabel.setText("");
					System.out.println("cleaned");
				}
			});

		} catch (InterruptedException e) {
			// e.printStackTrace();
		}
	}
}