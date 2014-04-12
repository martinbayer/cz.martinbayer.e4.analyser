package cz.martinbayer.e4.analyser.handlers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.EMenuService;

import cz.martinbayer.e4.analyser.ContextVariables;
import cz.martinbayer.e4.analyser.canvas.ICanvasManager;
import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.line.SerializableCanvasConnectionItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;
import cz.martinbayer.e4.analyser.widgets.processoritem.SerializableCanvasProcessorItem;

public class SaveProjectHandler {

	@Inject
	private Logger l;

	@Execute
	public void execute(
			IEclipseContext eclipseContext,
			EMenuService service,
			@Named(value = ContextVariables.CANVAS_OBJECTS_MANAGER) ICanvasManager canvasManager) {
		l.info("Saving project");
		List<IProcessorItem> processors = canvasManager.getProcessors();
		List<SerializableCanvasProcessorItem> serializableItems = getSerializableProcessors(processors);
		List<ILine> lines = canvasManager.getLines();
		List<SerializableCanvasConnectionItem> serializableLines = getSerializableLines(lines);
		File f = new File("c:\\out.obj");
		try (FileOutputStream fos = new FileOutputStream(f);
				FileChannel channel = fos.getChannel()) {

			ByteBuffer src = null;

			ByteArrayOutputStream baos = null;
			ObjectOutputStream oos = null;
			byte[] bytes = null;

			// create a byteArrayOutputStream to get byte[] finally
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(serializableItems);
			oos.writeObject(serializableLines);

			bytes = baos.toByteArray();

			// wrap those byte[] to ByteBuffer, which will be send over
			// channel
			// to persist
			src = ByteBuffer.wrap(bytes);

			if (channel.isOpen()) {
				channel.write(src);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private List<SerializableCanvasConnectionItem> getSerializableLines(
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

	private List<SerializableCanvasProcessorItem> getSerializableProcessors(
			List<IProcessorItem> processors) {
		if (processors != null) {
			ArrayList<SerializableCanvasProcessorItem> serializableProcessors = new ArrayList<>();
			for (IProcessorItem processor : processors) {
				serializableProcessors.add(new SerializableCanvasProcessorItem(
						processor));
			}
			return serializableProcessors;
		}
		return new ArrayList<>();
	}

	@CanExecute
	public boolean canExecute() {
		// TODO Your code goes here
		return true;
	}

}