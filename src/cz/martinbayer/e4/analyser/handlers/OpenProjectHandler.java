package cz.martinbayer.e4.analyser.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.EMenuService;
import org.osgi.framework.Bundle;

import cz.martinbayer.analyser.processorsPool.ProcessorsPool;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.CanvasMouseAdapter;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.canvas.MainCanvas;
import cz.martinbayer.e4.analyser.widgets.ICanvasItem;
import cz.martinbayer.e4.analyser.widgets.line.LinePart;
import cz.martinbayer.e4.analyser.widgets.line.SerializableCanvasConnectionItem;
import cz.martinbayer.e4.analyser.widgets.line.connection.ConnectionItem;
import cz.martinbayer.e4.analyser.widgets.line.connection.ItemConnectionConnector;
import cz.martinbayer.e4.analyser.widgets.processoritem.CanvasProcessorItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.SerializableCanvasProcessorItem;

public class OpenProjectHandler {
	@Execute
	public void execute(
			IEclipseContext eclipseContext,
			MApplication application,
			EMenuService menuService,
			@Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager canvasManager)
			throws ClassNotFoundException {
		List<Bundle> bundles = ProcessorsPool.getInstance().getProcBundles();
		List<IProcessorItem> processors = null;
		File file = new File("c:\\out.obj");
		try (FileInputStream in = new FileInputStream(file);
				ObjectInputStreamWithLoader ois = new ObjectInputStreamWithLoader(
						in, bundles.toArray(new Bundle[] {}))) {
			Object oo = ois.readObject();
			ArrayList<SerializableCanvasProcessorItem> items = (ArrayList<SerializableCanvasProcessorItem>) oo;

			oo = ois.readObject();
			MainCanvas mainCanvas = (MainCanvas) eclipseContext
					.get(ContextVariables.MAIN_CANVAS_COMPONENT);
			for (SerializableCanvasProcessorItem item : items) {
				ICanvasItem canvItem = CanvasMouseAdapter.createItem(
						mainCanvas.getInnerCanvasComposite(),
						item.getOrigPaletteItem(), item.getProcessorItem(),
						application, menuService, item.getLocation(), item
								.getProcessorItem().getProcessorLogic()
								.getProcessor().getName(),
						item.getProcessorId());

				mainCanvas.addItem(canvItem);
			}

			ArrayList<SerializableCanvasConnectionItem> conns = (ArrayList<SerializableCanvasConnectionItem>) oo;
			for (SerializableCanvasConnectionItem item : conns) {
				ConnectionItem connection = new ConnectionItem(
						mainCanvas.getInnerCanvasComposite(), application,
						menuService, item.getConnectionId());
				initConnectorsForConnection(item, connection, canvasManager);
				/* init coordinates for connection points */
				connection.setStartPoint(item.getStartPoint().x, item
						.getStartPoint().y, connection.getSourceItem()
						.getBounds());
				connection.setEndPoint(item.getEndPoint().x,
						item.getEndPoint().y, connection.getDestinationItem()
								.getBounds());
				connection.pack();
				connection.moveAbove(null);
				mainCanvas.addItem(connection);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initConnectorsForConnection(
			SerializableCanvasConnectionItem item, ConnectionItem connection,
			ICanvasManager canvasManager) {
		for (IProcessorItem processor : canvasManager.getProcessors()) {
			if (connection.getSourceItem() != null
					&& connection.getDestinationItem() != null) {
				break;
			}
			if (item.getSourceId().equals(processor.getItemId())) {
				processor.addConnection(new ItemConnectionConnector(connection,
						processor, LinePart.START_SPOT));
				connection.setSourceItem((CanvasProcessorItem) processor);

			} else if (item.getDestinationId().equals(processor.getItemId())) {
				processor.addConnection(new ItemConnectionConnector(connection,
						processor, LinePart.END_SPOT));
				connection.setDestinationItem((CanvasProcessorItem) processor);
			}
		}
	}

	@CanExecute
	public boolean canExecute() {
		// TODO Your code goes here
		return true;
	}

}

class ObjectInputStreamWithLoader extends ObjectInputStream {
	private ClassLoader loader;
	private Bundle[] bundles;

	/**
	 * Loader must be non-null;
	 */

	public ObjectInputStreamWithLoader(InputStream in, Bundle[] bundles)
			throws IOException, StreamCorruptedException {

		super(in);
		if (bundles == null) {
			throw new IllegalArgumentException(
					"Illegal null argument to ObjectInputStreamWithLoader");
		}
		this.bundles = bundles;
	}

	/**
	 * Use the given ClassLoader rather than using the system class
	 */
	@Override
	protected Class<?> resolveClass(ObjectStreamClass classDesc)
			throws IOException, ClassNotFoundException {
		String cname = classDesc.getName();
		Class<?> clazz = null;
		try {
			clazz = super.resolveClass(classDesc);
			/* exception not thrown so clazz was found */
			return clazz;
		} catch (ClassNotFoundException ex) {
			// ignore exception and go to check bundles
		}
		for (Bundle b : bundles) {
			try {
				clazz = b.loadClass(cname);
				/* exception not thrown so clazz was found */
				return clazz;

			} catch (ClassNotFoundException e) {
				// ignore exception and check another bundle or throw the
				// exception if there is no more bundles to check
			}
		}
		throw new ClassNotFoundException(
				"Unable to find a class locally and in bundles");

	}
}
