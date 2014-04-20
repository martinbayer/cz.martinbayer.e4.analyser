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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;

import cz.martinbayer.analyser.processorsPool.ProcessorsPool;
import cz.martinbayer.e4.analyser.ContextDefaultValues;
import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.canvas.CanvasMouseAdapter;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.canvas.MainCanvas;
import cz.martinbayer.e4.analyser.persistence.ActualFileHelper;
import cz.martinbayer.e4.analyser.persistence.SerializationHelper;
import cz.martinbayer.e4.analyser.widgets.ICanvasItem;
import cz.martinbayer.e4.analyser.widgets.line.LinePart;
import cz.martinbayer.e4.analyser.widgets.line.SerializableCanvasConnectionItem;
import cz.martinbayer.e4.analyser.widgets.line.connection.ConnectionItem;
import cz.martinbayer.e4.analyser.widgets.line.connection.ItemConnectionConnector;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.SerializableCanvasProcessorItem;
import cz.martinbayer.utils.StringUtils;

public class OpenProjectHandler {
	private static Logger logger = LoggerFactory
			.getInstance(OpenProjectHandler.class);

	@Execute
	public void execute(
			IEclipseContext eclipseContext,
			MApplication application,
			EMenuService menuService,
			@Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager canvasManager,
			@Preference(nodePath = ContextVariables.Property.PROJECT_OPERATION) IEclipsePreferences prefs,
			Shell shell) throws ClassNotFoundException {

		/*
		 * handle the situation when there is some scenario already opened or in
		 * progress. False is returned if 'Cancel' is pressed on 'Save changes?'
		 * dialog.
		 */
		if (!SerializationHelper.checkActuallProject(canvasManager,
				eclipseContext, shell)) {
			return;
		} else {
			canvasManager.removeAll();
		}
		/* open Open file dialog with initial value as directory */
		String openDialogPath = prefs.get(
				ContextVariables.Property.LAST_OPEN_DIR, "");
		FileDialog openDialog = new FileDialog(shell, SWT.SINGLE);
		openDialog.setFilterPath(openDialogPath);
		openDialog
				.setFilterExtensions(new String[] { ContextDefaultValues.PROJECT_EXTENSION });
		if (openDialog.open() == null) {
			return;
		}
		String directory = openDialog.getFilterPath();
		String fileName = openDialog.getFileName();
		File selectedFile = null;
		if (StringUtils.isEmtpy(directory)
				|| StringUtils.isEmtpy(fileName)
				|| !(selectedFile = new File(directory + File.separator
						+ fileName)).exists()) {
			MessageDialog.openError(shell, "Invalid file",
					"Please, select valid file.");
			return;
		}

		readObjects(selectedFile, eclipseContext, application, menuService,
				shell, canvasManager);
		prefs.put(ContextVariables.Property.LAST_OPEN_DIR, directory);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			logger.error(e, "Error occured during preferences flushing");
		}
	}

	private void readObjects(File selectedFile, IEclipseContext eclipseContext,
			MApplication application, EMenuService menuService, Shell shell,
			ICanvasManager canvasManager) throws ClassNotFoundException {
		/* read data from selected file */
		List<Bundle> bundles = ProcessorsPool.getInstance().getProcBundles();
		try (FileInputStream in = new FileInputStream(selectedFile);
				ObjectInputStreamWithLoader ois = new ObjectInputStreamWithLoader(
						in, bundles.toArray(new Bundle[] {}))) {
			Object oo = ois.readObject();
			ArrayList<SerializableCanvasProcessorItem> items = (ArrayList<SerializableCanvasProcessorItem>) oo;

			oo = ois.readObject();
			MainCanvas mainCanvas = (MainCanvas) eclipseContext
					.get(ContextVariables.MAIN_CANVAS_COMPONENT);
			for (SerializableCanvasProcessorItem item : items) {
				// item.resetData();
				ICanvasItem canvItem = CanvasMouseAdapter.createItem(
						mainCanvas.getInnerCanvasComposite(),
						item.getOrigPaletteItem(), item.getProcessorItem(),
						application, menuService, item.getLocation(), item
								.getProcessorItem().getProcessorLogic()
								.getProcessor().getName(),
						item.getProcessorId());
				item.getProcessorItem().getProcessorLogic().getProcessor()
						.init();
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
			/* no exception thrown so set actual file */
			ActualFileHelper.setActualFile(eclipseContext, selectedFile);
		} catch (IOException e) {
			MessageDialog.openError(shell, "Unable to open file",
					"Unable to open selected file.");
			logger.error(e, "Error ocured during project file opening");
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

			} else if (item.getDestinationId().equals(processor.getItemId())) {
				processor.addConnection(new ItemConnectionConnector(connection,
						processor, LinePart.END_SPOT));
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
	 * Use bundles to load particular class
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
				"Unable to find a class locally nor in bundles");

	}
}
