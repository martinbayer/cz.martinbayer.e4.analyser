package cz.martinbayer.e4.analyser.palette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.martinbayer.analyser.processors.IProcessorItemWrapper;
import cz.martinbayer.analyser.processors.model.IE4LogsisLog;
import cz.martinbayer.analyser.processors.types.LogProcessor;
import cz.martinbayer.e4.analyser.helper.ProcessorTypeResolver;

public class PaletteItemResolver {

	/**
	 * Creates particular roots for processors
	 * 
	 * @param processors
	 * @return
	 */
	public static List<RootPaletteItem> resolveProcessors(
			List<IProcessorItemWrapper<IE4LogsisLog>> processors) {
		HashMap<String, RootPaletteItem> roots = new HashMap<>();
		LogProcessor<IE4LogsisLog> proc = null;
		String procTypeName = null;
		for (IProcessorItemWrapper<IE4LogsisLog> processor : processors) {
			proc = processor.getProcessorLogic().getProcessor();
			procTypeName = ProcessorTypeResolver.getLabel(proc);
			if (!roots.containsKey(procTypeName)) {
				roots.put(procTypeName, new RootPaletteItem(procTypeName,
						ProcessorTypeResolver.getIcon(proc)));
			}
			RootPaletteItem actualRoot = roots.get(procTypeName);
			ProcessorPaletteItem newItem = new ProcessorPaletteItem(processor);
			newItem.setParent(actualRoot);
			actualRoot.addChild(newItem);
		}
		return new ArrayList<>(roots.values());
	}
}
