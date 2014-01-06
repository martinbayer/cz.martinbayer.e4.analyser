package cz.martinbayer.e4.analyser.palette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.martinbayer.analyser.logic.processor.LogProcessor;
import cz.martinbayer.analyser.processorsPool.ProcessorPluginWrapper;
import cz.martinbayer.e4.analyser.helper.ProcessorTypeResolver;

public class PaletteItemResolver {

	/**
	 * Creates particular roots for processors
	 * 
	 * @param processors
	 * @return
	 */
	public static List<RootPaletteItem> resolveProcessors(
			List<ProcessorPluginWrapper> processors) {
		HashMap<String, RootPaletteItem> roots = new HashMap<>();
		LogProcessor proc = null;
		String procTypeName = null;
		for (ProcessorPluginWrapper processor : processors) {
			proc = processor.getProcessorLogic().getProcessor();
			procTypeName = ProcessorTypeResolver.getLabel(proc);
			if (!roots.containsKey(procTypeName)) {
				roots.put(procTypeName, new RootPaletteItem(procTypeName,
						ProcessorTypeResolver.getIcon(proc)));
			}
			RootPaletteItem actualRoot = roots.get(procTypeName);
			ProcessorPaletteItem newItem = new ProcessorPaletteItem(
					processor.getPaletteItem(), processor.getProcessorLogic());
			newItem.setParent(actualRoot);
			actualRoot.addChild(newItem);
		}
		return new ArrayList<>(roots.values());
	}
}
