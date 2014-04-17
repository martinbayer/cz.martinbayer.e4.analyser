package cz.martinbayer.e4.analyser.canvas;

import java.util.UUID;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.EMenuService;
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
import org.eclipse.swt.widgets.Text;

import cz.martinbayer.analyser.processors.IProcessorItemWrapper;
import cz.martinbayer.analyser.processors.model.IE4LogsisLog;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.palette.ProcessorPaletteItem;
import cz.martinbayer.e4.analyser.widgets.ICanvasItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasItemDnDData;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;
import cz.martinbayer.utils.StringUtils;

public class CanvasMouseAdapter extends MouseAdapter {
	private Logger logger = LoggerFactory.getInstance(getClass());

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

	public static final CanvasProcessorItem createItem(Composite innerCanvas,
			ProcessorPaletteItem origPaletteItem,
			IProcessorItemWrapper<IE4LogsisLog> processorItem,
			MApplication application, EMenuService menuService, Point location,
			String defaultName, UUID processorId) {

		CanvasProcessorItem item = new CanvasProcessorItem(innerCanvas,
				SWT.NONE, origPaletteItem, processorItem, menuService,
				application.getContext(), processorId);
		Text t1 = new Text(innerCanvas, SWT.BORDER);
		/*
		 * text is added to item, also binding between item logic and text is
		 * created
		 */
		item.setText(t1);
		item.setLocation(location.x, location.y);
		item.pack();
		t1.pack();

		if (StringUtils.isEmtpy(defaultName)) {
			defaultName = ((CanvasObjectsManager) application.getContext().get(
					ContextVariables.CANVAS_OBJECTS_MANAGER))
					.getDefaultNameForProcessor(item);
		}
		t1.setText(defaultName);
		return item;

	}

	private void handleProcessorItemCreation(MouseEvent e,
			ProcessorPaletteItem selectedPaletteItem, Object hoveredCanvasItem) {
		if (hoveredCanvasItem == null) {
			CanvasProcessorItem item = createItem(canvas, selectedPaletteItem,
					selectedPaletteItem.getItemWrapper().getInstance(),
					application, menuService, new Point(e.x, e.y), null, null);
			mainCanvas.addItem(item);
		}
	}

	public void initForDND(final ICanvasItem item) {
		if (item instanceof CanvasProcessorItem) {
			Control control = (CanvasProcessorItem) item;

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
								.getProcessorPaletteItem()
								.getSmallImageDescriptor().createImage();
					} else {
						// Getting dimensions of this widget
						Point compositeSize = composite.getSize();
						// creating new GC
						GC gc = new GC(composite);
						image = new Image(Display.getCurrent(),
								compositeSize.x, compositeSize.y);
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
		} else {
			logger.info(
					"Only processor item can be uset for DND. Instead {0} used",
					item);
		}
	}
}
