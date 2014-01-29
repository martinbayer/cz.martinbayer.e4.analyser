package cz.martinbayer.e4.analyser.widgets.canvasitem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import cz.martinbayer.e4.analyser.canvas.utils.HighlightType;
import cz.martinbayer.e4.analyser.palette.ProcessorPaletteItem;
import cz.martinbayer.e4.analyser.widgets.line.connection.ItemConnectionConnector;

public class CanvasItem extends Composite implements Serializable, ICanvasItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2213613605192127483L;
	private GridLayout itemLayout;
	private Point imageSize;
	private ImageDescriptor image;
	private Point selectionOffsetPoint;
	private ProcessorPaletteItem item;
	private List<ItemConnectionConnector> connectionItems;
	private CanvasItemEventHandler eventHandler;

	@Inject
	private MApplication application;

	public CanvasItem(Composite parent, int style,
			ProcessorPaletteItem selectedObject) {
		this(parent, style, selectedObject, 40, 40);
	}

	public CanvasItem(Composite parent, int style,
			ProcessorPaletteItem selectedObject, int neededImageHeight,
			int neededImageWidth) {
		super(parent, style | SWT.BORDER);
		this.connectionItems = new ArrayList<>();
		this.item = selectedObject;
		this.imageSize = new Point(neededImageWidth, neededImageHeight);
		this.image = selectedObject.getImage();
		checkImage(this.image);
		initLayout();
		initListeners();
		setCursor(new Cursor(null, SWT.CURSOR_HAND));
	}

	@Override
	public void setHighlighted(HighlightType highlightType) {
		System.out.println(application == null);
		if (highlightType != null) {
			setBackground(getDisplay().getSystemColor(
					highlightType.getSwtColorCode()));
		} else {
			setBackground(null);
		}
	}

	private void initListeners() {
		eventHandler = new CanvasItemEventHandler(this);
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image.createImage(), 5, 5);
				e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_RED));
				e.gc.drawRectangle(0, 0, getBounds().width, getBounds().height);
			}
		});
		addMouseMoveListener(eventHandler);
		addMouseListener(eventHandler);
		addMouseTrackListener(eventHandler);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(imageSize.x + 15, imageSize.y + 15);
	}

	private void checkImage(ImageDescriptor image) {
		assert image.getImageData().height == imageSize.x
				&& image.getImageData().width == imageSize.y;
	}

	private void initLayout() {
		this.itemLayout = new GridLayout(3, false);
		setLayout(itemLayout);
	}

	public boolean containsPoint(int x, int y) {
		Rectangle r = new Rectangle(getBounds().x - 5, getBounds().y - 5,
				getBounds().width + 10, getBounds().height + 10);
		if (r.contains(x, y)) {
			return true;
		}
		return false;
	}

	public void setSelectionOffsetPoint(Point selectionOffsetPoint) {
		this.selectionOffsetPoint = selectionOffsetPoint;
	}

	public Point getSelectionOffsetPoint() {
		return this.selectionOffsetPoint;
	}

	public ProcessorPaletteItem getItem() {
		return item;
	}

	public void move(int moveX, int moveY) {
		Point actualLocation = getLocation();
		setLocation(actualLocation.x + moveX, actualLocation.y + moveY);
		for (ItemConnectionConnector connection : connectionItems) {
			connection.move(moveX, moveY);
		}
	}

	@Override
	public void addCanvasItemEventListener(ICanvasItemEventListener listener) {
		eventHandler.addListener(listener);
	}

	@Override
	public void removeCanvasItemEventListener(ICanvasItemEventListener listener) {
		eventHandler.removeListener(listener);
	}

	@Override
	public boolean addConnection(ItemConnectionConnector connector) {
		if (!this.connectionItems.contains(connector)) {
			this.connectionItems.add(connector);
			return true;
		}
		return false;
	}
}
