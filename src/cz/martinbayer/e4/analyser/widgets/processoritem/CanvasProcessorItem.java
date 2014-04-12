package cz.martinbayer.e4.analyser.widgets.processoritem;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import cz.martinbayer.analyser.processors.IProcessorItemWrapper;
import cz.martinbayer.analyser.processors.model.IXMLog;
import cz.martinbayer.analyser.processors.types.LogProcessor;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.Messages;
import cz.martinbayer.e4.analyser.canvas.utils.CanvasConnectorUtils;
import cz.martinbayer.e4.analyser.palette.ProcessorPaletteItem;
import cz.martinbayer.e4.analyser.swt.utils.ColorUtils;
import cz.martinbayer.e4.analyser.widgets.line.LinePart;
import cz.martinbayer.e4.analyser.widgets.line.connection.ConnectionItem;
import cz.martinbayer.e4.analyser.widgets.line.connection.ItemConnectionConnector;
import cz.martinbayer.utils.gui.SWTUtils;

public class CanvasProcessorItem extends Composite implements Serializable,
		IProcessorItem {
	private Logger logger = LoggerFactory.getInstance(getClass());
	/**
	 * 
	 */
	private final UUID PROCESSOR_ID;
	public static final Point DEFAULT_SIZE = new Point(30, 30);
	private static final long serialVersionUID = -2213613605192127483L;
	private GridLayout itemLayout;
	private Point imageSize;
	private ImageDescriptor image, disabledImage;
	private Point selectionOffsetPoint;
	private List<ItemConnectionConnector> connectors;
	private CanvasItemEventHandler eventHandler;
	private IEclipseContext context;
	private final Color hoveredDefaultColor = ColorUtils
			.getColor(SWT.COLOR_BLACK), selectedDefaultColor = ColorUtils
			.getColor(SWT.COLOR_DARK_GRAY), takenDefaultColor = ColorUtils
			.getColor(SWT.COLOR_BLUE);
	private boolean selected = false;
	private boolean taken = false;
	private boolean hovered = false;
	private Text text;
	private IProcessorItemWrapper<IXMLog> processorItem;
	private ProcessorPaletteItem origPaletteItem;

	public CanvasProcessorItem(Composite parent, int style,
			ProcessorPaletteItem origPaletteItem,
			IProcessorItemWrapper<IXMLog> processorItem,
			EMenuService menuService, IEclipseContext context, UUID processorId) {
		this(parent, style, origPaletteItem, processorItem, menuService,
				context, DEFAULT_SIZE.x, DEFAULT_SIZE.y, processorId);
	}

	public CanvasProcessorItem(Composite parent, int style,
			ProcessorPaletteItem origPaletteItem,
			IProcessorItemWrapper<IXMLog> processorItem,
			EMenuService menuService, IEclipseContext context,
			int neededImageHeight, int neededImageWidth, UUID processorId) {
		super(parent, style | SWT.BORDER);
		/* property value can be assigned only once */
		PROCESSOR_ID = processorId != null ? processorId : UUID.randomUUID();
		this.connectors = new ArrayList<>();
		this.origPaletteItem = origPaletteItem;
		this.processorItem = processorItem;
		initProcessorListener(this.processorItem);
		this.imageSize = new Point(neededImageWidth, neededImageHeight);
		this.image = origPaletteItem.getImage();
		this.disabledImage = origPaletteItem.getDisabledImage();
		this.context = context;
		initPopupMenu(menuService);
		checkImage(this.image);
		initLayout();
		initListeners();
		setCursor(new Cursor(null, SWT.CURSOR_HAND));
	}

	private void initPopupMenu(EMenuService menuService) {
		if (menuService == null) {
			logger.error("No menu service provided. Context menu will not work");
			return;
		}
		menuService.registerContextMenu(this,
				ContextVariables.ITEM_POPUP_MENU_ID);
	}

	private void initProcessorListener(IProcessorItemWrapper<IXMLog> item) {
		item.getProcessorLogic()
				.getProcessor()
				.addPropertyChangeListener(LogProcessor.PROPERTY_ENABLED,
						new PropertyChangeListener() {

							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								redraw();
							}
						});
	}

	@Override
	public void setHovered(boolean hovered) {
		this.hovered = hovered;
		redraw();
	}

	@Override
	public void setTaken(boolean taken) {
		this.taken = taken;
		redraw();
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
		redraw();
	}

	private void initListeners() {
		eventHandler = new CanvasItemEventHandler(this, context);
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (selected) {
					e.gc.setBackground(selectedDefaultColor);
				} else {
					e.gc.setBackground(ColorUtils
							.getColor(SWT.COLOR_WIDGET_BACKGROUND));
				}
				e.gc.fillRectangle(0, 0, getBounds().width, getBounds().height);
				if (taken) {
					e.gc.setBackground(takenDefaultColor);
					e.gc.fillRectangle(5, 5, getBounds().width - 10,
							getBounds().height - 10);
				}
				if (hovered) {
					e.gc.setBackground(hoveredDefaultColor);
					e.gc.fillRectangle(0, 0, getBounds().width,
							getBounds().height);
				}
				if (processorItem.getProcessorLogic().getProcessor()
						.isEnabled()) {
					e.gc.drawImage(image.createImage(), 10, 10);
				} else {
					e.gc.drawImage(disabledImage.createImage(), 10, 10);
				}
			}
		});
		addMouseMoveListener(eventHandler);
		addMouseListener(eventHandler);
		addMouseTrackListener(eventHandler);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(imageSize.x + 20, imageSize.y + 20);
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

	public void move(int moveX, int moveY) {
		Point actualLocation = getLocation();
		setLocation(actualLocation.x + moveX, actualLocation.y + moveY);
		for (ItemConnectionConnector connection : connectors) {
			connection.move(moveX, moveY);
		}
	}

	@Override
	public boolean addConnection(ItemConnectionConnector connector) {
		if (!connectionContained(connector) && canBeSetForProcessor(connector)) {
			this.connectors.add(connector);
			udpateSrcAndDest(connector);
			return true;
		}
		logger.warn(Messages.connectioncannotbeadded, connector.getPart()
				.toString());
		return false;
	}

	private boolean connectionContained(ItemConnectionConnector connector) {
		if (connector.getConnection().getSourceItem() == null) {
			// it doesn't make sense to check the connections if there is no
			// item specified for actually created connection
			return false;
		}
		for (ItemConnectionConnector conn : this.connectors) {
			IProcessorItem source1 = conn.getConnection().getSourceItem();
			IProcessorItem destination1 = conn.getConnection()
					.getDestinationItem();
			IProcessorItem source2 = connector.getConnection().getSourceItem();
			IProcessorItem destination2 = connector.getItem();

			// check that same processors are not already connected
			if (CanvasConnectorUtils.areConnectionsSame(source1, destination1,
					source2, destination2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check that the connection can be added to the actual item's processor. Is
	 * is compared to processor's max input and output processors
	 * 
	 * @param connector
	 * @return
	 */
	private boolean canBeSetForProcessor(ItemConnectionConnector connector) {
		if (connector.getPart() == LinePart.START_SPOT) {
			if (processorItem.getProcessorLogic().getProcessor()
					.canAddOutProc()) {
				return true;
			}
		} else if (connector.getPart() == LinePart.END_SPOT) {
			if (processorItem.getProcessorLogic().getProcessor().canAddInProc()) {
				return true;
			}
		}
		return false;
	}

	private void udpateSrcAndDest(ItemConnectionConnector connector) {
		// set source first
		if (connector.getConnection().getSourceItem() == null) {
			connector.getConnection().setSourceItem(
					(CanvasProcessorItem) connector.getItem());
		} else if (connector.getConnection().getDestinationItem() == null) {
			// set destination
			connector.getConnection().setDestinationItem(
					(CanvasProcessorItem) connector.getItem());
		}
	}

	@Override
	public void itemTaken(Object param) {
		// not important
	}

	@Override
	public void itemUntaken(Object param) {
		// not important
	}

	@Override
	public void itemHovered(Object param) {
		setHovered(param != null);
	}

	@Override
	public void itemSelected(Object param) {
		setSelected(true);
	}

	@Override
	public void itemUnselected(Object param) {
		setSelected(false);
	}

	@Override
	public void setSelected(boolean selected, Object param) {
		if (selected) {
			itemSelected(param);
		} else {
			itemUnselected(param);
		}
	}

	public boolean removeConnector(ConnectionItem connectionItem) {
		int connectorIndex = -1;
		for (int i = 0; i < this.connectors.size(); i++) {
			if (this.connectors.get(i).getConnection().equals(connectionItem)) {
				connectorIndex = i;
				break;
			}
		}
		if (connectorIndex >= 0) {
			this.connectors.remove(connectorIndex);
			logger.info("Connector {0} removed from processor {1}",
					connectionItem, this);
			return true;
		}
		return false;
	}

	@Override
	public boolean remove() {
		removeConnections();
		setVisible(false);
		eventHandler.itemDisposed();
		text.dispose();
		return true;
	}

	private void removeConnections() {
		ArrayList<ItemConnectionConnector> connectorsCopyArrayList = new ArrayList<>(
				connectors);
		for (ItemConnectionConnector connector : connectorsCopyArrayList) {
			/* remove source destination's connector reference */
			// connector.getConnection().removeItemsRefs(connector, this);
			connector.getConnection().remove();
		}
		this.connectors.clear();
		this.connectors = null;
	}

	public void setText(Text text) {
		this.text = text;
		this.text.setCursor(new Cursor(null, SWT.CURSOR_IBEAM));
		this.text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text t = (Text) e.widget;
				String s = ((Text) e.widget).getText();
				Point size = SWTUtils.getTextSize(t, s.length());
				t.setSize(size.x + 10, size.y);
				Point controlLocation = getLocation();
				t.setLocation(controlLocation.x + getSize().x / 2
						- t.getSize().x / 2, controlLocation.y + getSize().y);
			}
		});
		this.text.setSize(SWTUtils.getTextSize(text, 1));
		this.text.setLocation(
				getLocation().x + getSize().x / 2 - text.getSize().x / 2,
				getLocation().y + getSize().y);
		this.text.getParent().layout();

		/* bind the text value with name property of the processor */
		DataBindingContext ctx = new DataBindingContext();
		IObservableValue model = BeanProperties.value(LogProcessor.class,
				LogProcessor.PROPERTY_NAME).observe(
				this.processorItem.getProcessorLogic().getProcessor());
		IObservableValue target = WidgetProperties.text(SWT.Modify).observe(
				text);
		ctx.bindValue(target, model);
	}

	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		if (this.text != null) {
			this.text.setLocation(
					getLocation().x + getSize().x / 2 - text.getSize().x / 2,
					getLocation().y + getSize().y);
		}
	}

	@Override
	public IProcessorItemWrapper<IXMLog> getItem() {
		return processorItem;
	}

	@Override
	public ProcessorPaletteItem getOrigPaletteItem() {
		return origPaletteItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((PROCESSOR_ID == null) ? 0 : PROCESSOR_ID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CanvasProcessorItem other = (CanvasProcessorItem) obj;
		if (PROCESSOR_ID == null) {
			if (other.PROCESSOR_ID != null)
				return false;
		} else if (!PROCESSOR_ID.equals(other.PROCESSOR_ID))
			return false;
		return true;
	}

	@Override
	public final UUID getItemId() {
		return this.PROCESSOR_ID;
	}

}
