package cz.martinbayer.e4.analyser.canvas;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
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
import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.palette.ProcessorPaletteItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasItemDnDData;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;

public class CanvasMouseAdapter extends MouseAdapter {

	private Composite canvas;
	private ScrolledComposite mainComposite;
	private MApplication application;
	private MainCanvas mainCanvas;
	private EMenuService menuService;

	public CanvasMouseAdapter(MainCanvas mainCanvas, Composite canvas,
			ScrolledComposite mainComposite, MApplication application,
			EMenuService menuService) {
		this.mainCanvas = mainCanvas;
		this.canvas = canvas;
		this.mainComposite = mainComposite;
		this.application = application;
		this.menuService = menuService;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		Object selectedPaletteItem = application.getContext().get(
				ContextVariables.PALETTE_ITEM_SELECTED);
		Object hoveredCanvasItem = (application.getContext()
				.get(ContextVariables.CANVAS_ITEM_HOVERED));
		if (selectedPaletteItem instanceof ProcessorPaletteItem
				&& /*
					 * don 't do any operations if root in the tree is selected
					 */((ProcessorPaletteItem) selectedPaletteItem).getParent() != null) {
			handleProcessorItemCreation(e,
					(ProcessorPaletteItem) selectedPaletteItem,
					hoveredCanvasItem);
		}
	}

	private void handleProcessorItemCreation(MouseEvent e,
			ProcessorPaletteItem selectedPaletteItem, Object hoveredCanvasItem) {
		if (hoveredCanvasItem == null) {
			CanvasProcessorItem item = new CanvasProcessorItem(canvas,
					SWT.NONE, selectedPaletteItem, menuService,
					application.getContext());
			item.setLocation(e.x, e.y);
			item.pack();
			initDnD(item);
			mainComposite.setMinSize(canvas.computeSize(SWT.DEFAULT,
					SWT.DEFAULT));
			mainCanvas.addItem(item);
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
				if (composite instanceof CanvasProcessorItem) {
					image = ((CanvasProcessorItem) composite).getItem()
							.getImage().createImage();
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
				CanvasProcessorItem selectedItem = (CanvasProcessorItem) ((DragSource) event
						.getSource()).getControl();
				event.offsetX = event.x;
				event.offsetY = event.y;
				Point offsetPoint = new Point(event.offsetX, event.offsetY);
				dragStartPoint = new Point(selectedItem.getLocation().x
						+ event.x, selectedItem.getLocation().y + event.y);
				selectedItem.setSelectionOffsetPoint(offsetPoint);
				application.getContext().set(
						ContextVariables.CANVAS_PROCESSOR_TAKEN,
						new CanvasEvent<CanvasProcessorItem>(selectedItem,
								event));
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
				dragStartPoint = null;
			}
		});
	}
}
