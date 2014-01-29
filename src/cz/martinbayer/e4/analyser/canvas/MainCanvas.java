package cz.martinbayer.e4.analyser.canvas;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.utils.CanvasItemsLocator;
import cz.martinbayer.e4.analyser.palette.ConnectionPaletteItem;
import cz.martinbayer.e4.analyser.palette.PaletteItem;
import cz.martinbayer.e4.analyser.palette.ProcessorPaletteItem;
import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItem;
import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItemActionEvent;
import cz.martinbayer.e4.analyser.widgets.canvasitem.CanvasItemDnDData;
import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.line.connection.ConnectionItem;
import cz.martinbayer.utils.ImageUtils;

public class MainCanvas implements ICanvas {

	private ArrayList<CanvasItem> canvasItems;
	private ScrolledComposite scrolledComposite;

	@Inject
	private MApplication application;
	private CanvasMouseAdapter canvasMouseAdapter;

	@Inject
	public MainCanvas() {
		canvasItems = new ArrayList<>();
	}

	@PostConstruct
	public void postConstruct(Composite parent) {

		parent.getShell().setLayout(new FillLayout());
		parent.setLayout(new FillLayout());
		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);

		Composite child = new Composite(scrolledComposite, SWT.NONE);

		child.setLayout(null);
		initListeners(child);
		initDnDTarget(child);

		scrolledComposite.setContent(child);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setMinSize(child
				.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public void addItem(CanvasItem item) {
		canvasItems.add(item);
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
				CanvasItem selectedItem = (CanvasItem) application.getContext()
						.get(CanvasMouseAdapter.SELECTED_CANVAS_ITEM);
				if (selectedItem != null) {
					DropTarget dt = (DropTarget) event.widget;
					Point offset = selectedItem.getSelectionOffsetPoint();

					Point movementPoint;
					if (event.data instanceof CanvasItemDnDData) {
						movementPoint = ((CanvasItemDnDData) event.data)
								.getSourcePoint();
						Point newLocation = dt.getControl().toControl(
								new Point(event.x, event.y));

						selectedItem.move(newLocation.x - movementPoint.x,
								newLocation.y - movementPoint.y);
						CanvasItemsLocator.normalizeLocations(canvasItems);
					}
				}
			}

		});
	}

	@Inject
	public void changeCursor(
			@Optional @Named(ContextVariables.PALETTE_SELECTED_ITEM) PaletteItem item) {
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
				this.scrolledComposite, application);
		canvas.addMouseListener(canvasMouseAdapter);

	}

	public ArrayList<CanvasItem> getItems() {
		return this.canvasItems;
	}

	@Override
	public void setSelectedItem(CanvasItemActionEvent e, boolean selected) {
		if (selected) {
			application.getContext().set(
					CanvasMouseAdapter.SELECTED_CANVAS_ITEM, e.getSource());
			canvasMouseAdapter.handleConnectionCreation((MouseEvent) e
					.getOriginalEvent());
		} else {
			application.getContext().set(
					CanvasMouseAdapter.SELECTED_CANVAS_ITEM, null);

		}
	}

	@Override
	public CanvasItem getSelectedItem() {
		CanvasItem selectedItem = (CanvasItem) application.getContext().get(
				CanvasMouseAdapter.SELECTED_CANVAS_ITEM);
		return selectedItem;
	}

	@Override
	public void setSelectedConnection(ILine selectedConnection) {
		application.getContext()
				.set(CanvasMouseAdapter.SELECTED_CONNECTION_ITEM,
						selectedConnection);
	}

	@Override
	public ConnectionItem getSelectedConnection() {
		ConnectionItem selectedItem = (ConnectionItem) application.getContext()
				.get(CanvasMouseAdapter.SELECTED_CONNECTION_ITEM);
		return selectedItem;
	}

	@Override
	public void setVisitedItem(CanvasItem visitedItem) {
		application.getContext().set(CanvasMouseAdapter.VISITED_CANVAS_ITEM,
				visitedItem);
	}

	public CanvasItem getVisitedItem() {
		return (CanvasItem) application.getContext().get(
				CanvasMouseAdapter.VISITED_CANVAS_ITEM);
	}
}
