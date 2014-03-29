package cz.martinbayer.e4.analyser.canvas;

import java.lang.reflect.Field;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.canvas.utils.CanvasItemsLocator;
import cz.martinbayer.e4.analyser.palette.ConnectionPaletteItem;
import cz.martinbayer.e4.analyser.palette.PaletteItem;
import cz.martinbayer.e4.analyser.palette.ProcessorPaletteItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasItemDnDData;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;
import cz.martinbayer.utils.ImageUtils;

public class MainCanvas extends CanvasEventHandler {

	private ScrolledComposite scrolledComposite;
	private Logger logger = LoggerFactory.getInstance(getClass());

	private CanvasMouseAdapter canvasMouseAdapter;
	private EMenuService menuService;
	private Composite canvasInnerComposite;

	@PostConstruct
	public void postConstruct(
			Composite parent,
			EMenuService menuService,
			@Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager canvasManager) {
		super.postConstruct(canvasManager);
		this.menuService = menuService;
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayout(new FillLayout());
		scrolledComposite = new ScrolledComposite(mainComp, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);

		canvasInnerComposite = new Composite(scrolledComposite, SWT.NONE);

		canvasInnerComposite.setLayout(null);
		initListeners(canvasInnerComposite);
		initDnDTarget(canvasInnerComposite);

		scrolledComposite.setContent(canvasInnerComposite);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setMinSize(canvasInnerComposite.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));
	}

	private void initDnDTarget(final Composite canvas) {
		Transfer[] types = new Transfer[] { FakeTransfer.getInstance() };
		DropTarget target = new DropTarget(canvas, DND.DROP_MOVE);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(DropTargetEvent event) {
				// get the selected item from the application context. Item is
				// set to the context in CanvasMouseAdapter
				CanvasProcessorItem selectedItem = (CanvasProcessorItem) ((CanvasEvent) application
						.getContext().get(
								ContextVariables.CANVAS_PROCESSOR_TAKEN))
						.getItem();
				if (selectedItem != null) {
					DropTarget dt = (DropTarget) event.widget;

					Point movementPoint;
					if (event.data instanceof CanvasItemDnDData) {
						movementPoint = ((CanvasItemDnDData) event.data)
								.getSourcePoint();
						Point newLocation = dt.getControl().toControl(
								new Point(event.x, event.y));

						selectedItem.move(newLocation.x - movementPoint.x,
								newLocation.y - movementPoint.y);
						CanvasItemsLocator.normalizeLocations(canvasManager
								.getProcessors());
					}
				}
			}

		});
	}

	@Inject
	public void changeCursor(
			@Optional @Named(ContextVariables.PALETTE_ITEM_SELECTED) PaletteItem item) {
		if (scrolledComposite != null && !scrolledComposite.isDisposed()) {
			if (item != null) {
				Bundle bundle = FrameworkUtil.getBundle(this.getClass());
				if (item instanceof ProcessorPaletteItem) {
					ImageData cursorImage = ImageUtils.getImage(
							"icons/pin.png", bundle, 0, 0).getImageData();

					// new cursor is created with described image and the
					// point is in the middle of the bottom edge
					Cursor c = new Cursor(null, cursorImage,
							cursorImage.width / 2, cursorImage.height - 1);
					scrolledComposite.setCursor(c);

				} else if (item instanceof ConnectionPaletteItem) {
					scrolledComposite.setCursor(new Cursor(null,
							SWT.CURSOR_CROSS));
				}
			} else {
				scrolledComposite.setCursor(null);
			}
		}
	}

	private void initListeners(final Composite canvas) {
		canvasMouseAdapter = new CanvasMouseAdapter(this, canvas,
				this.scrolledComposite, application, this.menuService);
		canvas.addMouseListener(canvasMouseAdapter);

	}

	@Focus
	public void setFocus() {
		scrolledComposite.setFocus();
	}

	@PreDestroy
	void preDestroy(IEclipseContext context) {
		Field[] fields = ContextVariables.class.getDeclaredFields();
		for (Field f : fields) {
			try {
				Object val = f.get(null);
				if (val instanceof String) {
					context.set((String) val, null);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.warn("Error during context clean-up");
			}
		}
	}

	@Override
	public Composite getInnerCanvasComposite() {
		return this.canvasInnerComposite;
	}

	@Override
	public ScrolledComposite getScrollingOuterCanvasComposite() {
		return this.scrolledComposite;
	}
}
