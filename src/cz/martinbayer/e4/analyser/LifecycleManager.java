package cz.martinbayer.e4.analyser;

import java.util.ArrayList;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.slf4j.LoggerFactory;

import cz.martinbayer.e4.analyser.canvas.CanvasObjectsManager;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.log.SLF4JLoggerWrapper;
import cz.martinbayer.e4.analyser.statusbar.StatusHandler;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;

public class LifecycleManager implements ContextVariables, ContextDefaultValues {

	@PostContextCreate
	void postContextCreate(IEclipseContext context) {
		initSLF4J(context);
		initializeContext(context);
		initStatusHandler(context);
	}

	/**
	 * Status handler uses event broker for notifying application about actual
	 * application status, errors etc...
	 * 
	 * @param context
	 */
	private void initStatusHandler(IEclipseContext context) {
		/* initialize event proker for status info panel */
		ContextInjectionFactory.inject(StatusHandler.getInstance(), context);
	}

	private void initSLF4J(IEclipseContext context) {
		// Use SLF4J as LoggerProvider for e4
		ILoggerProvider service = new ILoggerProvider() {
			@Override
			public org.eclipse.e4.core.services.log.Logger getClassLogger(
					Class<?> clazz) {
				return new SLF4JLoggerWrapper(LoggerFactory.getLogger(clazz));
			}
		};
		/* it can be gained via ILoggerProvider from application context */
		context.set(ILoggerProvider.class, service);

		/*
		 * initialization of logger factory - logger can be gained via its
		 * static method getInstance
		 */
		cz.martinbayer.e4.analyser.LoggerFactory
				.initializeLoggerFactory(service);
	}

	private void initializeContext(IEclipseContext context) {
		/* initialized default repository location */
		context.set(REPOSITORY_LOCATION_KEY, REPOSITORY_LOCATION_VALUE);

		/* remember all input processors to be able to start the processes */
		ArrayList<CanvasProcessorItem> items = new ArrayList<>();
		context.set(CANVAS_INPUT_ITEMS, items);

		ICanvasManager canvasObjectsManager = CanvasObjectsManager
				.getInstance();
		context.set(CANVAS_OBJECTS_MANAGER, canvasObjectsManager);
	}
}
