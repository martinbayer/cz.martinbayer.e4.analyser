package cz.martinbayer.e4.analyser.canvas;

import java.util.List;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;

import cz.martinbayer.e4.analyser.canvas.event.CanvasEvent;
import cz.martinbayer.e4.analyser.widgets.ICanvasItem;
import cz.martinbayer.e4.analyser.widgets.line.ILine;
import cz.martinbayer.e4.analyser.widgets.processoritem.IProcessorItem;

public interface ICanvasManager {

	List<ILine> getLines();

	List<IProcessorItem> getProcessors();

	void selectItem(ICanvasItem item, boolean select);

	void takeItem(ICanvasItem item, boolean take);

	boolean addLine(ILine line);

	boolean removeLine(ILine line);

	/***
	 * Processor is added only if same processor is not already contained and
	 * True is returned. If it is contained, False is returned.
	 * 
	 * @param processor
	 * @return
	 */
	boolean addProcessor(IProcessorItem processor);

	boolean removeProcessor(IProcessorItem processor);

	void hoverItem(CanvasEvent<ICanvasItem> event);

	List<IProcessorItem> getInputProcessors();

	String getDefaultNameForProcessor(IProcessorItem procItem);

	boolean usePreviousData(MApplication application, MWindow window);

	boolean removeAll();

	/**
	 * Clear data for all input processors but processor passed as argument
	 * 
	 * @param inputProcItem
	 *            - data for this processor won't be cleared
	 */
	void clearDataForInputProcsBut(IProcessorItem inputProcItem);
}
