package cz.martinbayer.e4.analyser.canvas;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.palette.ConnectionPaletteItem;
import cz.martinbayer.e4.analyser.palette.ProcessorPaletteItem;
import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItem;
import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItemDnDData;
import cz.martinbayer.e4.analyser.widgets.line.LinePart;
import cz.martinbayer.e4.analyser.widgets.line.connection.ConnectionItem;
import cz.martinbayer.e4.analyser.widgets.line.connection.ItemConnectionConnector;

public class CanvasMouseAdapter extends MouseAdapter {

	public static final String SELECTED_CANVAS_ITEM = "selected_canvas_item";
	public static final String SELECTED_CONNECTION_ITEM = "selected_connection_item";
	public static final String VISITED_CANVAS_ITEM = "visited_canvas_item";

	private Composite canvas;
	private ScrolledComposite mainComposite;
	private CanvasItem selectedItem;
	private MApplication application;
	private MainCanvas mainCanvas;

	public CanvasMouseAdapter(MainCanvas mainCanvas, Composite canvas,
			ScrolledComposite mainComposite, MApplication application) {
		this.mainCanvas = mainCanvas;
		this.canvas = canvas;
		this.mainComposite = mainComposite;
		this.application = application;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		Object selectedPaletteItem = application.getContext().get(
				ContextVariables.PALETTE_SELECTED_ITEM);
		if (selectedPaletteItem instanceof ProcessorPaletteItem
				&& ((ProcessorPaletteItem) selectedPaletteItem).getParent() != null) {
			handleProcessorItemCreation(e,
					(ProcessorPaletteItem) selectedPaletteItem);
		} else {
			handleConnectionCreation(e);
		}

	}

	public void handleProcessorItemCreation(MouseEvent e,
			ProcessorPaletteItem selectedPaletteItem) {
		CanvasItem item = new CanvasItem(canvas, SWT.NONE, selectedPaletteItem);
		item.setLocation(e.x, e.y);
		item.addCanvasItemEventListener(new CanvasItemEventListener(mainCanvas));
		item.pack();
		initDnD(item);
		mainComposite.setMinSize(canvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		mainCanvas.addItem(item);
		application.getContext().set(ContextVariables.PALETTE_SELECTED_ITEM,
				null);
	}

	public void handleConnectionCreation(MouseEvent e) {
		ConnectionItem connection = null;
		CanvasItem selectedItem;
		Object selectedPaletteItem = application.getContext().get(
				ContextVariables.PALETTE_SELECTED_ITEM);
		if (selectedPaletteItem instanceof ConnectionPaletteItem) {
			if ((selectedItem = mainCanvas.getSelectedItem()) != null) {
				int xCoord = selectedItem.getLocation().x + e.x;
				int yCoord = selectedItem.getLocation().y + e.y;
				if ((connection = (ConnectionItem) application.getContext()
						.get(ConnectionItem.ACTIVE_CONNECTION)) == null) {
					connection = new ConnectionItem(canvas, SWT.NONE);
					// canvas is notified about the operations made over the
					// line
					connection
							.addLineEventListener(new CanvasLineEventListener(
									mainCanvas));
					if (mainCanvas.getSelectedItem().addConnection(
							new ItemConnectionConnector(connection,
									selectedItem, LinePart.START_SPOT))) {
						connection.setStartPoint(xCoord, yCoord,
								selectedItem.getBounds());
						application.getContext().set(
								ConnectionItem.ACTIVE_CONNECTION, connection);
					}
				} else {
					// do not allow to add the connection to the same item
					if (mainCanvas.getVisitedItem().addConnection(
							new ItemConnectionConnector(connection,
									selectedItem, LinePart.END_SPOT))) {
						connection.setEndPoint(xCoord, yCoord,
								selectedItem.getBounds());
						connection.pack();
						mainComposite.setMinSize(canvas.computeSize(
								SWT.DEFAULT, SWT.DEFAULT));
						application.getContext().set(
								ContextVariables.PALETTE_SELECTED_ITEM, null);
						application.getContext().set(
								ConnectionItem.ACTIVE_CONNECTION, null);
					}
				}
				connection.moveAbove(null);
			}
		}
	}

	private void initDnD(final Control control) {
		Transfer[] types = new Transfer[] { FakeTransfer.getInstance() };
		int operations = DND.DROP_MOVE;
		DragSource source = new DragSource(control, operations);
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {

			Point dragStartPoint = null;

			@Override
			public void dragStart(DragSourceEvent event) {
				// getting control widget - Composite in this case
				Composite composite = (Composite) ((DragSource) event
						.getSource()).getControl();
				// Creating new Image
				Image image;
				if (composite instanceof CanvasItem) {
					image = ((CanvasItem) composite).getItem().getImage()
							.createImage();
				} else {
					// Getting dimensions of this widget
					Point compositeSize = composite.getSize();
					// creating new GC
					GC gc = new GC(composite);
					image = new Image(Display.getCurrent(), compositeSize.x,
							compositeSize.y);
					// Rendering widget to image
					gc.copyArea(image, 0, 0);
				}
				// Setting widget to DnD image
				event.image = image;
				selectedItem = (CanvasItem) ((DragSource) event.getSource())
						.getControl();
				event.offsetX = event.x;
				event.offsetY = event.y;
				Point offsetPoint = new Point(event.offsetX, event.offsetY);
				dragStartPoint = new Point(selectedItem.getLocation().x
						+ event.x, selectedItem.getLocation().y + event.y);
				selectedItem.setSelectionOffsetPoint(offsetPoint);
				application.getContext()
						.set(SELECTED_CANVAS_ITEM, selectedItem);
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				CanvasItemDnDData data = new CanvasItemDnDData();
				data.setSourcePoint(dragStartPoint);
				event.data = data;
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				mainComposite.setMinSize(canvas.computeSize(SWT.DEFAULT,
						SWT.DEFAULT));
				selectedItem = null;
				dragStartPoint = null;
			}
		});
	}
}
