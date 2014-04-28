package cz.martinbayer.e4.analyser.canvas;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Composite;

import cz.martinbayer.analyser.processors.model.IE4LogsisLog;
import cz.martinbayer.analyser.processors.types.LogProcessor;
import cz.martinbayer.e4.analyser.Constants;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.Messages;
import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.palette.ConnectionPaletteItem;
import cz.martinbayer.e4.analyser.statusbar.StatusHandler;
import cz.martinbayer.e4.analyser.statusbar.StatusInfo;
import cz.martinbayer.e4.analyser.widgets.ICanvasItem;
import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.line.LinePart;
import cz.martinbayer.e4.analyser.widgets.line.connection.ConnectionItem;
import cz.martinbayer.e4.analyser.widgets.line.connection.ItemConnectionConnector;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public abstract class CanvasEventHandler implements ICanvas {

	private Logger logger = LoggerFactory.getInstance(getClass());

	@Inject
	protected MApplication application;

	@Inject
	protected EMenuService menuService;

	@Inject
	protected IEclipseContext ctx;

	protected ICanvasManager canvasManager;

	@Override
	public CanvasProcessorItem getSelectedProcessorItem() {
		return null;
	}

	@Override
	public ConnectionItem getSelectedConnection() {
		return null;
	}

	@Override
	@Inject
	@Optional
	public void canvasItemHovered(
			@UIEventTopic(ContextVariables.CANVAS_ITEM_HOVERED) CanvasEvent<ICanvasItem> event) {
		ctx.set(ContextVariables.CANVAS_ITEM_HOVERED, event);
		if (canvasManager == null) {
			logger.warn("Objects manager is not available yet.");
			return;
		}
		if (event != null) {
			canvasManager.hoverItem(event);
		} else {/*
				 * main thought is that if context is changed for some key and
				 * the new value is NULL, then all operations like selection,
				 * hover etc. are discarded for all such items
				 */
			canvasManager.hoverItem(null);
		}
	}

	@Override
	@Inject
	@Optional
	public void canvasProcessorTaken(
			@UIEventTopic(value = ContextVariables.CANVAS_PROCESSOR_TAKEN) CanvasEvent<IProcessorItem> event) {
		ctx.set(ContextVariables.CANVAS_PROCESSOR_TAKEN, event);
		if (canvasManager == null) {
			logger.warn("Objects manager is not available yet.");
			return;
		}
		if (event != null && event.getItem() != null) {
			canvasManager.takeItem(event.getItem(), true);
		} else {/*
				 * main thought is that if context is changed for some key and
				 * the new value is NULL, then all operations like selection,
				 * hover etc. are discarded for all such items
				 */
			canvasManager.takeItem(null, false);
		}
	}

	@Override
	@Inject
	@Optional
	public void canvasProcessorSelected(
			@UIEventTopic(ContextVariables.CANVAS_PROCESSOR_SELECTED) CanvasEvent<IProcessorItem> processorEvent) {
		ctx.set(ContextVariables.CANVAS_PROCESSOR_SELECTED, processorEvent);
		ctx.set(ContextVariables.CANVAS_CONNECTION_SELECTED, null);
		if (canvasManager == null) {
			logger.warn("Objects manager is not available yet.");
			return;
		}
		if (processorEvent != null && processorEvent.getItem() != null) {
			canvasManager.selectItem(processorEvent.getItem(), true);
			Object selectedPaletteItem = application.getContext().get(
					ContextVariables.PALETTE_ITEM_SELECTED);
			if (selectedPaletteItem instanceof ConnectionPaletteItem) {
				TypedEvent origEvent = processorEvent.getOrigEvent();
				if (origEvent instanceof MouseEvent
						&& ((MouseEvent) origEvent).button == Constants.LEFT_MOUSE_BUTTON) {
					handleConnectionCreation(
							(MouseEvent) processorEvent.getOrigEvent(),
							(ConnectionPaletteItem) selectedPaletteItem,
							processorEvent.getItem());
				}
			}
		} else {/*
				 * main thought is that if context is changed for some key and
				 * the new value is NULL, then all operations like selection,
				 * hover etc. are discarded for all such items
				 */
			canvasManager.selectItem(null, false);
		}
	}

	@Override
	@Inject
	@Optional
	public void canvasConnectionTaken(
			@UIEventTopic(value = ContextVariables.CANVAS_CONNECTION_TAKEN) CanvasEvent<ILine> connectionItem) {
		if (canvasManager == null) {
			logger.warn("Objects manager is not available yet.");
			return;
		}
	}

	@Override
	@Inject
	@Optional
	public void canvasConnectionSelected(
			@UIEventTopic(value = ContextVariables.CANVAS_CONNECTION_SELECTED) CanvasEvent<ILine> connectionEvent) {
		ctx.set(ContextVariables.CANVAS_CONNECTION_SELECTED, connectionEvent);
		ctx.set(ContextVariables.CANVAS_PROCESSOR_SELECTED, null);
		if (canvasManager == null) {
			logger.warn("Objects manager is not available yet.");
			return;
		}
		if (connectionEvent != null) {
			canvasManager.selectItem(connectionEvent.getItem(), true);
		} else {/*
				 * main thought is that if context is changed for some key and
				 * the new value is NULL, then all operations like selection,
				 * hover etc. are discarded for all such items
				 */
			canvasManager.selectItem(null, false);
		}
	}

	public void postConstruct(ICanvasManager canvasManager) {
		if (canvasManager != null) {
			this.canvasManager = canvasManager;
		} else {
			throw new RuntimeException(
					"Context is not available for canvas operations");
		}
	}

	@Inject
	@Optional
	@Override
	public void canvasItemDisposed(
			@UIEventTopic(ContextVariables.CANVAS_DISPOSED_ITEM) CanvasEvent<ICanvasItem> canvasEvent) {
		if (canvasManager == null) {
			logger.warn("Objects manager is not available yet.");
			return;
		}
		ICanvasItem item = null;
		if (canvasEvent != null && (item = canvasEvent.getItem()) != null) {
			if (item instanceof ILine) {
				// remove proper processor instances
				LogProcessor<IE4LogsisLog> sourceProcessor = ((ConnectionItem) item)
						.getSourceItem().getItem().getProcessorLogic()
						.getProcessor();
				LogProcessor<IE4LogsisLog> destinationProcessor = ((ConnectionItem) item)
						.getDestinationItem().getItem().getProcessorLogic()
						.getProcessor();
				if (!sourceProcessor.removeProcessor(destinationProcessor)) {
					logger.error("Unable to remove processor instance. Errors can occur");
				}
				canvasManager.removeLine((ILine) item);
			} else if (item instanceof IProcessorItem) {
				canvasManager.removeProcessor((IProcessorItem) item);
			}
		}
	}

	private void handleConnectionCreation(MouseEvent e,
			ConnectionPaletteItem selectedPaletteItem, Object hoveredCanvasItem) {
		ConnectionItem connection = null;

		/* create connection only if the mouse is above some processor */
		if (hoveredCanvasItem instanceof CanvasProcessorItem
				&& selectedPaletteItem instanceof ConnectionPaletteItem) {
			CanvasProcessorItem hoveredItem = (CanvasProcessorItem) hoveredCanvasItem;
			int xCoord = hoveredItem.getLocation().x + e.x;
			int yCoord = hoveredItem.getLocation().y + e.y;
			// connection is null because we're creating first point of this
			// connection
			if ((connection = (ConnectionItem) application.getContext().get(
					ContextVariables.CANVAS_CONNECTION_CREATING)) == null) {
				connection = new ConnectionItem(getInnerCanvasComposite(),
						application, menuService, null);
				if (hoveredItem.addConnection(new ItemConnectionConnector(
						connection, hoveredItem, LinePart.START_SPOT))) {
					connection.setStartPoint(xCoord, yCoord,
							hoveredItem.getBounds());
					application.getContext().set(
							ContextVariables.CANVAS_CONNECTION_CREATING,
							connection);
				} else {
					StatusInfo info = new StatusInfo();
					info.setStatusMessage(Messages.connectioncannotbeadded,
							LinePart.START_SPOT);
					StatusHandler.setStatus(info);
					application.getContext().set(
							ContextVariables.CANVAS_CONNECTION_CREATING, null);
				}
			} else {
				// do not allow to add the connection to the same item and
				// check the possible count of connections for each item
				if (hoveredItem.addConnection(new ItemConnectionConnector(
						connection, hoveredItem, LinePart.END_SPOT))) {
					connection.setEndPoint(xCoord, yCoord,
							hoveredItem.getBounds());
					connection.pack();
					getScrollingOuterCanvasComposite().setMinSize(
							getInnerCanvasComposite().computeSize(SWT.DEFAULT,
									SWT.DEFAULT));
					application.getContext().set(
							ContextVariables.CANVAS_CONNECTION_CREATING, null);
					application.getContext().set(
							ContextVariables.PALETTE_ITEM_SELECTED, null);
					addItem(connection);
				} else {
					StatusInfo info = new StatusInfo();
					info.setStatusMessage(Messages.connectioncannotbeadded,
							LinePart.END_SPOT);
					StatusHandler.setStatus(info);
					application.getContext().set(
							ContextVariables.CANVAS_CONNECTION_CREATING, null);
				}
			}
			// move the connection to the top on the canvas
			connection.moveAbove(null);
		}
	}

	public boolean addItem(ICanvasItem item) {
		boolean inserted = false;
		if (item instanceof IProcessorItem) {
			inserted = canvasManager.addProcessor((IProcessorItem) item);
		} else if (item instanceof ILine) {
			inserted = canvasManager.addLine((ILine) item);
			if (inserted) {
				LogProcessor<IE4LogsisLog> sourceProcessor = ((ConnectionItem) item)
						.getSourceItem().getItem().getProcessorLogic()
						.getProcessor();
				LogProcessor<IE4LogsisLog> destinationProcessor = ((ConnectionItem) item)
						.getDestinationItem().getItem().getProcessorLogic()
						.getProcessor();
				sourceProcessor.addNextProcessor(destinationProcessor);
			}
		} else {
			logger.error("Unable to add item which is null or invalid type {}",
					item);
			inserted = false;
		}
		if (inserted) {
			return true;
		} else {
			item.remove();
			return false;
		}
	}

	public abstract Composite getInnerCanvasComposite();

	public abstract ScrolledComposite getScrollingOuterCanvasComposite();
}
