package cz.martinbayer.e4.analyser.statusbar;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;

import cz.martinbayer.e4.analyser.ContextVariables;

public class StatusHandler {

	private static IEventBroker broker;
	private static StatusHandler instance;

	public static final void setStatus(StatusInfo status) {
		if (instance == null) {
			getInstance();
		}
		broker.send(ContextVariables.APP_STATUS, status);
	}

	@Inject
	private static void initEventBroker(IEventBroker broker) {
		StatusHandler.broker = broker;
	}

	public static StatusHandler getInstance() {
		if (instance == null) {
			instance = new StatusHandler();
		}
		return instance;
	}
}
