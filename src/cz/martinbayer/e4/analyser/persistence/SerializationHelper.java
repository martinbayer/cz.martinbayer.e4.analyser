package cz.martinbayer.e4.analyser.persistence;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.handlers.SaveProjectHandler;
import cz.martinbayer.e4.analyser.serialization.SerialClone;
import cz.martinbayer.e4.analyser.statusbar.StatusInfo;
import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.line.SerializableCanvasConnectionItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.SerializableCanvasProcessorItem;

public class SerializationHelper {
	private static Logger logger = LoggerFactory
			.getInstance(SerializationHelper.class);

	public static final StatusInfo saveProject(File saveFile,
			List<IProcessorItem> processors, List<ILine> lines) {
		List<SerializableCanvasProcessorItem> serializableItems = getSerializableProcessors(processors);
		List<SerializableCanvasConnectionItem> serializableLines = getSerializableLines(lines);
		try (FileOutputStream fos = new FileOutputStream(saveFile);
				FileChannel channel = fos.getChannel()) {

			ByteBuffer src = null;

			ByteArrayOutputStream baos = null;
			ObjectOutputStream oos = null;
			byte[] bytes = null;

			// create a byteArrayOutputStream to get byte[] finally
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			/* serialize all processor items */
			oos.writeObject(serializableItems);
			/* serialize all connection items */
			oos.writeObject(serializableLines);

			bytes = baos.toByteArray();
			src = ByteBuffer.wrap(bytes);

			if (channel.isOpen()) {
				channel.write(src);
			}
			return null;
		} catch (IOException e) {
			logger.error(e, "Unable to save project with Save action");
			return new StatusInfo()
					.setStatusMessage("Unable to save project to file:"
							+ saveFile.getAbsolutePath());
		}
	}

	private static List<SerializableCanvasConnectionItem> getSerializableLines(
			List<ILine> lines) {
		if (lines != null) {
			ArrayList<SerializableCanvasConnectionItem> serializableConns = new ArrayList<>();
			for (ILine line : lines) {
				serializableConns
						.add(new SerializableCanvasConnectionItem(line));
			}
			return serializableConns;
		}
		return new ArrayList<>();
	}

	private static List<SerializableCanvasProcessorItem> getSerializableProcessors(
			List<IProcessorItem> processors) {
		if (processors != null) {
			ArrayList<SerializableCanvasProcessorItem> serializableProcessors = new ArrayList<>();
			for (IProcessorItem processor : processors) {
				SerializableCanvasProcessorItem serializableProcItem = new SerializableCanvasProcessorItem(
						processor);
				serializableProcItem = SerialClone.clone(serializableProcItem);
				serializableProcItem.resetData();
				serializableProcessors.add(serializableProcItem);
			}
			return serializableProcessors;
		}
		return new ArrayList<>();
	}

	/**
	 * Returns True if everything is OK
	 */
	public static boolean checkActuallProject(ICanvasManager canvasManager,
			IEclipseContext eclipseContext, Shell shell) {
		/*
		 * if there are some processors or lines on canvas, ask for saving them
		 * or stop the process with Cancel
		 */
		if (canvasManager.getProcessors().size() > 0
				|| canvasManager.getLines().size() > 0) {
			MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION
					| SWT.YES | SWT.NO | SWT.CANCEL);
			dialog.setText("Save changes?");
			dialog.setMessage("There is unsaved project in progress. Do you want to save it?");
			int returnCode = dialog.open();
			switch (returnCode) {
			case SWT.YES:
				executeSave(eclipseContext);
				return true;
			case SWT.NO:
				return true;
			case SWT.CANCEL:
				return false;
			}
		}

		return true;
	}

	private static void executeSave(IEclipseContext eclipseContext) {
		/* save actual project */
		ECommandService commandService = eclipseContext
				.get(ECommandService.class);
		EHandlerService handlerService = eclipseContext
				.get(EHandlerService.class);

		ParameterizedCommand myCommand = commandService.createCommand(
				SaveProjectHandler.SAVE_PROJECT_COMMAND, null);
		handlerService.executeHandler(myCommand);
	}

}
