package cz.martinbayer.e4.analyser.canvas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.e4.core.services.log.Logger;

import cz.martinbayer.analyser.processors.types.InputProcessor;
import cz.martinbayer.e4.analyser.LoggerFactory;
import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.canvas.event.LineEvent;
import cz.martinbayer.e4.analyser.widgets.ICanvasItem;
import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public class CanvasObjectsManager implements ICanvasManager {
	private Logger logger = LoggerFactory.getInstance(getClass());
	private ConcurrentHashMap<Class<?>, AtomicInteger> procTypes = new ConcurrentHashMap<>();
	private HashSet<String> procNames = new HashSet<>();

	private static CanvasObjectsManager instance;
	private List<ILine> lines = new ArrayList<>();
	private List<IProcessorItem> processors = new ArrayList<>();

	@Override
	public List<ILine> getLines() {
		return lines;
	}

	@Override
	public List<IProcessorItem> getProcessors() {
		return processors;
	}

	@Override
	public void selectItem(ICanvasItem item, boolean select) {
		/* deselect all items first */
		for (ICanvasItem i : this.getProcessors()) {
			i.setSelected(false, null);
		}
		for (ICanvasItem i : this.getLines()) {
			i.setSelected(false, null);
		}
		if (item != null) {
			item.setSelected(select, null);
		}
	}

	@Override
	public void hoverItem(CanvasEvent<ICanvasItem> event) {
		/* deselect all items first */
		for (ICanvasItem i : this.getProcessors()) {
			i.itemHovered(null);
		}
		for (ICanvasItem i : this.getLines()) {
			i.itemHovered(null);
		}
		if (event != null) {
			ICanvasItem item = event.getItem();
			if (item instanceof ILine) {
				LineEvent<ILine> e = (LineEvent) event;
				item.itemHovered(e.getLinePart());
			}
		}
	}

	@Override
	public void takeItem(ICanvasItem item, boolean take) {
		/* deselect all items first */
		for (ICanvasItem i : this.getProcessors()) {
			i.itemUntaken(null);
		}
		if (item != null) {
			item.itemTaken(null);
		}
	}

	public static synchronized ICanvasManager getInstance() {
		if (instance == null) {
			instance = new CanvasObjectsManager();
		}
		return instance;
	}

	@Override
	public boolean addLine(ILine line) {
		if (!this.lines.contains(line)) {
			this.lines.add(line);
			logger.info("Line added {0}", line);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeLine(ILine line) {
		if (this.lines.remove(line)) {
			logger.info("Line {0} succesfully removed", line);
			return true;
		}
		return false;
	}

	@Override
	public boolean addProcessor(IProcessorItem processor) {
		if (!this.processors.contains(processor)) {
			this.processors.add(processor);
			this.procNames.add(processor.getItem().getProcessorLogic()
					.getProcessor().getName());
			if (!procTypes.containsKey(processor.getItem().getProcessorLogic()
					.getProcessor().getClass())) {
				procTypes.put(processor.getItem().getProcessorLogic()
						.getProcessor().getClass(), new AtomicInteger(1));
			} else {
				procTypes.get(
						processor.getItem().getProcessorLogic().getProcessor()
								.getClass()).incrementAndGet();
			}
			logger.info("Processor added {0}", processor);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeProcessor(IProcessorItem processor) {
		if (this.processors.remove(processor)) {
			/*
			 * do not decrement the counter because if one element is added,
			 * second element is added and then first is removed and one more is
			 * added, there would be two processors with the same name
			 */
			// procTypes.get(
			// processor.getItem().getProcessorLogic().getProcessor()
			// .getClass()).decrementAndGet();
			procNames.remove(processor.getItem().getProcessorLogic()
					.getProcessor().getName());
			logger.info("Processor {0} succesfully removed", processor
					.getItem().getProcessorLogic().getProcessor().getName());
			return true;
		}
		return false;
	}

	@Override
	public List<IProcessorItem> getInputProcessors() {
		List<IProcessorItem> result = new ArrayList<>();
		for (IProcessorItem item : processors) {
			if (item.getItem().getProcessorLogic().getProcessor() instanceof InputProcessor) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public String getDefaultNameForProcessor(IProcessorItem procItem) {
		StringBuffer sbName = new StringBuffer();
		/* use label name for the processor */
		sbName.append(procItem.getItem().getProcessorPaletteItem().getLabel());
		sbName.append("_");
		AtomicInteger index = new AtomicInteger();
		while (true) {
			String name = new StringBuffer(sbName).append(
					String.valueOf(index.incrementAndGet())).toString();
			if (!procNames.contains(name)) {
				return name;
			}
		}
	}
}
