package cz.martinbayer.e4.analyser.helper;

import cz.martinbayer.analyser.logic.processor.ConditionalProcessor;
import cz.martinbayer.analyser.logic.processor.InputProcessor;
import cz.martinbayer.analyser.logic.processor.LogProcessor;
import cz.martinbayer.analyser.logic.processor.OutputProcessor;

public class ProcessorTypeResolver {
	public enum ProcessorType {
		INPUT_PROCESSOR("input_processor", "input_processor"), LOG_PROCESSOR(
				"log_processor", "log_processor"), OUTPUT_PROCESSOR(
				"output_processor", "output_processor"), CONDITIONAL_PROCESSOR(
				"conditional_processor", "conditional_processor");

		private String iconName;
		private String typeName;

		ProcessorType(String typeName, String iconName) {
			this.typeName = typeName;
			this.iconName = iconName;
		}

		public String getIconName() {
			return iconName;
		}

		public String getTypeName() {
			return typeName;
		}
	}

	public static ProcessorType getType(LogProcessor p) {
		if (p instanceof InputProcessor) {
			return ProcessorType.INPUT_PROCESSOR;
		}

		if (p instanceof ConditionalProcessor) {
			return ProcessorType.CONDITIONAL_PROCESSOR;
		}

		if (p instanceof OutputProcessor) {
			return ProcessorType.OUTPUT_PROCESSOR;
		}
		return null;
	}

	public static String getIcon(LogProcessor p) {
		StringBuffer iconName = new StringBuffer();
		iconName.append(getType(p).getIconName());
		iconName.append(".png");
		return iconName.toString();
	}

	public static String getLabel(LogProcessor p) {
		return getType(p).getTypeName();
	}
}
