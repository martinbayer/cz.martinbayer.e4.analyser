package cz.martinbayer.e4.analyser.helper;

import cz.martinbayer.analyser.processors.model.IE4LogsisLog;
import cz.martinbayer.analyser.processors.types.ConditionalProcessor;
import cz.martinbayer.analyser.processors.types.InputProcessor;
import cz.martinbayer.analyser.processors.types.LogProcessor;
import cz.martinbayer.analyser.processors.types.OutputProcessor;

public class ProcessorTypeResolver {
	public enum ProcessorType {
		INPUT_PROCESSOR("Input processor", "input_processor"), LOG_PROCESSOR(
				"Log processor", "log_processor"), OUTPUT_PROCESSOR(
				"Output processor", "output_processor"), CONDITIONAL_PROCESSOR(
				"Conditional processor", "conditional_processor");

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

	public static ProcessorType getType(LogProcessor<IE4LogsisLog> p) {
		if (p instanceof InputProcessor) {
			return ProcessorType.INPUT_PROCESSOR;
		}

		if (p instanceof ConditionalProcessor) {
			return ProcessorType.CONDITIONAL_PROCESSOR;
		}

		if (p instanceof OutputProcessor) {
			return ProcessorType.OUTPUT_PROCESSOR;
		}

		if (p instanceof LogProcessor<?>) {
			return ProcessorType.LOG_PROCESSOR;
		}
		return null;
	}

	public static String getIcon(LogProcessor<IE4LogsisLog> p) {
		StringBuffer iconName = new StringBuffer();
		iconName.append(getType(p).getIconName());
		iconName.append(".png");
		return iconName.toString();
	}

	public static String getLabel(LogProcessor p) {
		return getType(p).getTypeName();
	}
}
