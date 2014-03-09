package cz.martinbayer.e4.analyser;

public interface ContextVariables {

	/* palette items selections */
	public static final String PALETTE_ITEM_SELECTED = "cz.martinbayer.e4.analyser.palette_item_selected";

	public static final String CANVAS_CONNECTION_CREATING = "cz.martinbayer.e4.analyse.canvas_connection_creating";

	/* canvas items selections */
	public static final String CANVAS_PROCESSOR_SELECTED = "cz_martinbayer_e4_analyser_canvas_processor_selected";
	/*
	 * only for mouse down, it is set to null when mouse goes up or mouse is
	 * exited
	 */
	public static final String CANVAS_PROCESSOR_TAKEN = "cz_martinbayer_e4_analyser_canvas_processor_taken";
	public static final String CANVAS_CONNECTION_SELECTED = "cz_martinbayer_e4_analyser_canvas_connection_selected";

	public static final String CANVAS_CONNECTION_TAKEN = "cz_martinbayer_e4_analyser_canvas_connection_taken";
	/* canvas items hovers */
	public static final String CANVAS_ITEM_HOVERED = "cz_martinbayer_e4_analyser_canvas_item_hovered";

	/* all input processors collection */
	public static final String CANVAS_INPUT_ITEMS = "cz.martinbayer.e4.analyser.canvas.inputItems";
	public static final String REPOSITORY_LOCATION_KEY = "cz.martinbayer.e4.analyser.repoloc";

	/*
	 * all canvas items (processors and connections) are handled using this
	 * object
	 */
	public static final String CANVAS_OBJECTS_MANAGER = "cz.martinbayer.e4.analyser.canvas_objects_manager";

	public static final String CANVAS_DISPOSED_ITEM = "cz_martinbayer_e4_analyser_CANVAS_DISPOSED_ITEM";

	public static final String APP_STATUS = "cz_martinbayer_e4_analyser_application_status";

	/* other IDs */
	public static final String ITEM_POPUP_MENU_ID = "cz_martinbayer_e4_analyser_processor_popup_menu";
}
